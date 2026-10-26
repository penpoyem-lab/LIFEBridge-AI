import ipaddress
import re
import socket
from urllib.parse import urlparse
from typing import List, Tuple
from app.core.logging import logger

class RiskAnalysisService:
    """
    Threat intelligence & Scam detection service.
    Implements robust SSRF prevention, URL structure analysis, and linguistic fraud detection.
    """

    # Prohibited IP networks for SSRF protection
    BLOCKED_NETWORKS = [
        ipaddress.ip_network("0.0.0.0/8"),
        ipaddress.ip_network("10.0.0.0/8"),
        ipaddress.ip_network("100.64.0.0/10"),
        ipaddress.ip_network("127.0.0.0/8"),
        ipaddress.ip_network("169.254.0.0/16"), # Link-local / Cloud Metadata (AWS, GCP, Azure)
        ipaddress.ip_network("172.16.0.0/12"),
        ipaddress.ip_network("192.0.0.0/24"),
        ipaddress.ip_network("192.168.0.0/16"),
        ipaddress.ip_network("::1/128"),
        ipaddress.ip_network("fc00::/7"),
        ipaddress.ip_network("fe80::/10"),
    ]

    SHORTENER_DOMAINS = {"bit.ly", "tinyurl.com", "cutt.ly", "is.gd", "t.co", "ow.ly", "buff.ly", "rb.gy"}

    @classmethod
    def validate_url_safe_and_not_ssrf(cls, url: str) -> Tuple[bool, str]:
        """
        Validates that a URL does not point to internal infrastructure, localhost,
        or cloud metadata endpoints (SSRF mitigation).
        """
        try:
            parsed = urlparse(url)
            if parsed.scheme not in ("http", "https"):
                return False, "Unsupported protocol: only HTTP and HTTPS are permitted."

            hostname = parsed.hostname
            if not hostname:
                return False, "Invalid URL: missing domain hostname."

            # Check if direct IP address
            try:
                ip_obj = ipaddress.ip_address(hostname)
                for net in cls.BLOCKED_NETWORKS:
                    if ip_obj in net:
                        return False, f"Prohibited address: {hostname} points to private/internal network (SSRF protection)."
            except ValueError:
                # Hostname is a domain name, resolve DNS
                try:
                    resolved_ips = socket.getaddrinfo(hostname, None)
                    for res in resolved_ips:
                        ip_str = res[4][0]
                        ip_obj = ipaddress.ip_address(ip_str)
                        for net in cls.BLOCKED_NETWORKS:
                            if ip_obj in net:
                                return False, f"Security Block: Domain {hostname} resolves to private IP {ip_str}."
                except Exception as e:
                    logger.info(f"DNS lookup resolution error for {hostname}: {str(e)}")
                    # Allow analysis of the syntax even if DNS is unreachable

            return True, "URL passed security sanity validation."
        except Exception as e:
            return False, f"URL parse exception: {str(e)}"

    @classmethod
    def analyze_message_or_link(cls, content: str, check_type: str = "Message") -> dict:
        lower = content.lower()
        score = 8
        indicators: List[str] = []
        safe_steps: List[str] = []

        is_link_check = check_type.lower() == "link" or content.startswith("http://") or content.startswith("https://")

        if is_link_check:
            # Safe URL validation & SSRF check
            is_safe, msg = cls.validate_url_safe_and_not_ssrf(content)
            if not is_safe:
                return {
                    "check_type": "Link",
                    "input_snippet": content[:100],
                    "risk_level": "High Risk",
                    "score": 95,
                    "indicators": [msg, "Target matches prohibited network or dangerous protocol scheme."],
                    "safe_steps": ["Do not attempt to open this link in any browser.", "Block and report the sender."],
                    "explanation": f"Security restriction: {msg}"
                }

            # URL syntactic risk check
            parsed = urlparse(content)
            host = (parsed.hostname or "").lower()

            if any(host == d or host.endswith("." + d) for d in cls.SHORTENER_DOMAINS):
                score += 35
                indicators.append(f"Obfuscated / Shortened link service detected ({host}). Real final destination is obscured.")

            if content.startswith("http://"):
                score += 25
                indicators.append("Insecure plain HTTP protocol (lacks SSL/TLS encryption).")

            if re.search(r'\b(?:\d{1,3}\.){3}\d{1,3}\b', host):
                score += 45
                indicators.append("Direct IP address used as website host instead of registered domain name.")

            if any(k in lower for k in ["kyc", "login", "verify", "secure", "banking", "pan-update", "lottery", "claim"]):
                score += 30
                indicators.append("Domain or path uses financial / credential verification keywords commonly found in phishing.")

            safe_steps.append("Never enter bank credentials, passwords, or OTPs on links sent via SMS or chat.")
            safe_steps.append("Navigate to the service directly by typing its known official web address.")
            safe_steps.append("Verify the website certificate lock in your browser address bar.")
        else:
            # Message Analysis
            urgency_patterns = [
                r"\b(?:urgent|immediately|within 24 hours|account blocked|suspended|cancelled today|final notice|forfeit)\b",
                r"\b(?:immediate action required|act now|expires in \d+ hours)\b"
            ]
            prize_patterns = [
                r"\b(?:lottery|jackpot|won|winner|cash reward|lucky draw|free gift|congratulations you have won)\b",
                r"\b(?:claim your ₹|claim your \$|cashback of ₹)\b"
            ]
            credential_patterns = [
                r"\b(?:otp|one time password|cvv|atm pin|netbanking password|kyc update|share your pan|aadhaar link)\b"
            ]
            payment_patterns = [
                r"\b(?:pay fee|advance fee|processing charge|transfer money to|send gift card|crypto transfer)\b"
            ]

            if any(re.search(p, lower) for p in urgency_patterns):
                score += 30
                indicators.append("Psychological urgency language detected (designed to induce panic and bypass caution).")

            if any(re.search(p, lower) for p in prize_patterns):
                score += 35
                indicators.append("Unsolicited financial prize or lottery award claim.")

            if any(re.search(p, lower) for p in credential_patterns):
                score += 40
                indicators.append("Solicitation of sensitive personal credentials, OTPs, or identity numbers.")

            if any(re.search(p, lower) for p in payment_patterns):
                score += 30
                indicators.append("Request for upfront processing fee or money transfer.")

            if re.search(r'https?://|bit\.ly/|tinyurl\.com/', lower):
                score += 20
                indicators.append("Contains embedded link prompt leading to external unverified destination.")

            safe_steps.append("Never share OTPs, PINs, or card CVVs with anyone, even if they claim to be bank officials.")
            safe_steps.append("Contact the issuing institution directly via their verified website or phone helpline.")
            safe_steps.append("Block and report the sender's number to the national cybercrime helpline.")

        score = min(max(score, 5), 98)
        risk_level = "High Risk" if score >= 60 else "Warning Signs" if score >= 35 else "No Obvious Signs"
        explanation = (
            "Multiple high-risk fraud indicators detected. This item exhibits classic deceptive patterns."
            if risk_level == "High Risk"
            else "Some warning indicators were recognized. Exercise caution and verify via official independent channels."
            if risk_level == "Warning Signs"
            else "No standard fraud markers were detected. However, remain vigilant with unsolicited contacts."
        )

        return {
            "check_type": "Link" if is_link_check else "Message",
            "input_snippet": content[:100],
            "risk_level": risk_level,
            "score": score,
            "indicators": indicators,
            "safe_steps": safe_steps,
            "explanation": explanation
        }

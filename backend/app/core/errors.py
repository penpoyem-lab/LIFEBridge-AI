from fastapi import Request, status
from fastapi.responses import JSONResponse
from app.core.logging import logger

class LifeBridgeException(Exception):
    def __init__(self, message: str, status_code: int = status.HTTP_400_BAD_REQUEST):
        self.message = message
        self.status_code = status_code
        super().__init__(self.message)

async def lifebridge_exception_handler(request: Request, exc: LifeBridgeException):
    logger.warning(f"Handled application exception on {request.url.path}: {exc.message}")
    return JSONResponse(
        status_code=exc.status_code,
        content={"error": True, "detail": exc.message, "path": request.url.path}
    )

async def global_exception_handler(request: Request, exc: Exception):
    logger.error(f"Unhandled server error on {request.url.path}: {str(exc)}", exc_info=True)
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content={"error": True, "detail": "An internal error occurred. Please try again later."}
    )

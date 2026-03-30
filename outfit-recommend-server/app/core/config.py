from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    clip_model_name: str = "openai/clip-vit-base-patch32"
    device: str = "cpu"

    class Config:
        env_file = ".env"


settings = Settings()

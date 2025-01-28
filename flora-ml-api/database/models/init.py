import asyncio
from sqlalchemy.ext.asyncio import AsyncSession, create_async_engine
from sqlalchemy.orm import sessionmaker
from user import Base as UserBase
from blacklisted_tokens import Base as BlacklistedTokensBase
from database.db_config import DATABASE_URL
from user_observations import Base as UserObservationsBase
from user_observations_encrypted import Base as UserObservationsEncrypted
async def create_tables():
    engine = create_async_engine(DATABASE_URL, echo=True)
    async_session = sessionmaker(engine, expire_on_commit=False, class_=AsyncSession)

    async with async_session() as session:
        async with engine.begin() as conn:
            await conn.run_sync(UserBase.metadata.create_all)
            await conn.run_sync(BlacklistedTokensBase.metadata.create_all)
            await conn.run_sync(UserObservationsBase.metadata.create_all)
            await conn.run_sync(UserObservationsEncrypted.metadata.create_all)


async def main():
    await create_tables()


if __name__ == "__main__":
    asyncio.run(main())

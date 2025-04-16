package data.testutils

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

object SchemaSetup {
    fun createAuthCredentialsTableWithConstraint(authCredentials: Table) {
        transaction {
            SchemaUtils.create(authCredentials)

            exec("""
            DO $$
            BEGIN
                IF NOT EXISTS (
                    SELECT 1 FROM pg_constraint WHERE conname = 'provider_consistency_check'
                ) THEN
                    ALTER TABLE auth_credentials
                    ADD CONSTRAINT provider_consistency_check
                    CHECK (
                        (provider = 'REGULAR' AND google_id IS NULL AND password IS NOT NULL) OR
                        (provider = 'GOOGLE' AND google_id IS NOT NULL AND password IS NULL)
                    );
                END IF;
            END
            $$;
        """.trimIndent())
        }
    }

    fun createCarModelsTable(carModels: Table) {
        transaction {
            SchemaUtils.create(carModels)
        }
    }

    fun createCommentsTable(comments: Table) {
        transaction {
            SchemaUtils.create(comments)
        }
    }

    fun createFriendRequestsTableWithConstraint(friendRequests: Table) {
        transaction {
            SchemaUtils.create(friendRequests)

            exec("""
            DO $$
            BEGIN
                IF NOT EXISTS (
                    SELECT 1 FROM pg_constraint WHERE conname = 'friend_request_check'
                ) THEN
                    ALTER TABLE friend_requests
                    ADD CONSTRAINT friend_request_check
                    CHECK (sender_id <> receiver_id);
                END IF;
            END
            $$;
        """.trimIndent())
        }
    }

    fun createFriendsTableWithConstraint(friends: Table) {
        transaction {
            SchemaUtils.create(friends)

            exec("""
            DO $$
            BEGIN
                IF NOT EXISTS (
                    SELECT 1 FROM pg_constraint WHERE conname = 'chk_no_self_friendship'
                ) THEN
                    ALTER TABLE friends
                    ADD CONSTRAINT chk_no_self_friendship
                    CHECK (user_id <> friend_id);
                END IF;
            END
            $$;
        """.trimIndent())
        }
    }

    fun createLikesTable(likes: Table) {
        transaction {
            SchemaUtils.create(likes)
        }
    }

    fun createPostsTable(posts: Table) {
        transaction {
            SchemaUtils.create(posts)
        }
    }

    fun createUsersTable(users: Table) {
        transaction {
            SchemaUtils.create(users)
        }
    }

    fun createUsersCarsTable(usersCars: Table) {
        transaction {
            SchemaUtils.create(usersCars)
        }
    }

}
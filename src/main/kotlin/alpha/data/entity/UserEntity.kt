package alpha.data.entity

import alpha.common.Status
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var username by Users.username
    var password by Users.password
    var name by Users.name
    var email by Users.email
    var imageUrl by Users.imageUrl
    var sub by Users.sub
    var serviceType by Users.serviceType
    var role by Users.role
    var status by Users.status
    var createdAt by Users.createdAt
    var updatedAt by Users.updatedAt
}

object Users : IntIdTable("users") {
    val username = varchar("username", 255).nullable()
    val password = varchar("password", 255).nullable()
    val name = varchar("name", 255)
    val email = varchar("email", 255).nullable()
    val imageUrl = text("image_url").nullable()
    val sub = varchar("sub", 255).nullable()
    val serviceType = byte("service_type")
    val role = byte("role")
    val status = byte("status").default(Status.ACTIVE.number)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}
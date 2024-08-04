package alpha.config

import alpha.common.Role
import io.ktor.server.application.*
import io.ktor.server.routing.*

inline fun Route.withRoles(vararg roles: Role, crossinline build: Route.() -> Unit) {
    val route = createChild(RoleRouteSelector())
    route.install(RbacPlugin) {
        addRequireRoles(*roles)
    }
    route.build()
}

class RoleRouteSelector : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent
}

class RbacConfiguration {
    val requiredRoles = mutableSetOf<Role>()

    fun addRequireRoles(vararg roles: Role) {
        requiredRoles.addAll(roles)
    }
}

val RbacPlugin = createRouteScopedPlugin("rbacPlugin", ::RbacConfiguration) {
    val requiredRoles = pluginConfig.requiredRoles
    // validate logic
}
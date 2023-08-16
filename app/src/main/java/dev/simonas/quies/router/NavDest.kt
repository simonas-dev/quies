package dev.simonas.quies.router

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

internal interface NavDest {
    val route: String
    val arguments: List<NamedNavArgument>
}

internal fun navDest(
    destKey: String,
    argKeys: List<String>,
): NavDest =
    object : NavDest {
        override val route: String = "$destKey/" + argKeys.map { "{$it}/" }
        override val arguments: List<NamedNavArgument> = argKeys.map {
            navArgument(
                name = it,
                builder = {
                    type = NavType.StringType
                }
            )
        }
    }

internal fun NavDest.build(args: Map<String, String>): String {
    var path = route
    args.forEach { arg ->
        path = path.replace("{${arg.key}}", arg.value)
    }
    return path
}

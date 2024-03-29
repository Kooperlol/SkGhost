package me.kooper.skghost.elements.expressions

import ch.njol.skript.Skript
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.ExpressionType
import ch.njol.skript.lang.SkriptParser
import ch.njol.skript.lang.util.SimpleExpression
import ch.njol.util.Kleenean
import me.kooper.ghostcore.models.ChunkedView
import me.kooper.ghostcore.utils.types.SimplePosition
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.Event


@Suppress("UnstableApiUsage")
class ExprBlockLocation : SimpleExpression<Material>() {

    private lateinit var view: Expression<ChunkedView>
    private lateinit var location: Expression<Location>

    companion object {
        init {
            Skript.registerExpression(
                ExprBlockLocation::class.java,
                Material::class.java, ExpressionType.COMBINED, "[the] ghost block at %location% in view %view%"
            )
        }
    }

    override fun toString(event: Event?, debug: Boolean): String {
        return "Block location of view expression with expression location: ${
            location.toString(
                event,
                debug
            )
        } and expression view: ${view.toString(event, debug)}"
    }

    @Suppress("UNCHECKED_CAST")
    override fun init(
        expressions: Array<out Expression<*>>?,
        matchedPattern: Int,
        isDelayed: Kleenean?,
        parser: SkriptParser.ParseResult?
    ): Boolean {
        location = expressions!![0] as Expression<Location>
        view = expressions[1] as Expression<ChunkedView>
        return true
    }

    override fun isSingle(): Boolean {
        return true
    }

    override fun getReturnType(): Class<out Material> {
        return Material::class.java
    }

    override fun get(event: Event?): Array<Material?> {
        if (location.getSingle(event) == null || view.getSingle(event) == null) {
            return arrayOf(null)
        }

        val loc = SimplePosition.from(
            location.getSingle(event)!!.blockX,
            location.getSingle(event)!!.blockY,
            location.getSingle(event)!!.blockZ
        )

        if (!(view.getSingle(event)!!).hasBlock(loc)) {
            return arrayOf(null)
        }

        val view = view.getSingle(event)!!

        return arrayOf(view.getBlock(loc)?.block?.material)
    }

}
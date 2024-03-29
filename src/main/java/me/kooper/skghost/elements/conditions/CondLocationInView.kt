package me.kooper.skghost.elements.conditions

import ch.njol.skript.Skript
import ch.njol.skript.lang.Condition
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import io.papermc.paper.math.Position
import me.kooper.ghostcore.models.ChunkedView
import me.kooper.ghostcore.utils.types.SimplePosition
import org.bukkit.Location
import org.bukkit.event.Event


@Suppress("UnstableApiUsage")
class CondLocationInView : Condition() {

    companion object {
        init {
            Skript.registerCondition(
                CondLocationInView::class.java,
                "%location% (1¦is|2¦is(n't| not)) inside [of] view %view%"
            )
        }
    }

    private lateinit var location: Expression<Location>
    private lateinit var view: Expression<ChunkedView>

    @Suppress("UNCHECKED_CAST")
    override fun init(
        expressions: Array<Expression<*>>,
        matchedPattern: Int,
        isDelayed: Kleenean?,
        parser: SkriptParser.ParseResult?
    ): Boolean {
        location = expressions[0] as Expression<Location>
        view = expressions[1] as Expression<ChunkedView>
        isNegated = parser!!.mark == 1
        return true
    }

    override fun toString(event: Event?, debug: Boolean): String {
        return "Block inside view: ${location.toString(event, debug)} ${
            view.toString(
                event,
                debug
            )
        }"
    }

    override fun check(event: Event?): Boolean {
        if (location.getSingle(event) == null || view.getSingle(event) == null) return !isNegated
//        return if ((view.getSingle(event)!! as ChunkedView).blocks[Position.block(location.getSingle(event)!!)] != null) isNegated else !isNegated
        val chunkedView = view.getSingle(event)!!
        val block = Position.block(location.getSingle(event)!!)
        return chunkedView.hasBlock(
            SimplePosition.from(
                block.blockX(),
                block.blockY(),
                block.blockZ()
            )
        ) != isNegated
    }

}
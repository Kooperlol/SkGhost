package me.kooper.skghost.elements.effects

import ch.njol.skript.Skript
import ch.njol.skript.lang.Effect
import ch.njol.skript.lang.Expression
import ch.njol.skript.lang.SkriptParser
import ch.njol.util.Kleenean
import me.kooper.ghostcore.models.ChunkedStage
import me.kooper.ghostcore.models.ChunkedView
import me.kooper.ghostcore.utils.types.SimplePosition
import me.kooper.skghost.SkGhost
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.Event

@Suppress("UnstableApiUsage")
class EffSetAir : Effect() {

    companion object {
        init {
            Skript.registerEffect(
                EffSetAir::class.java,
                "set %locations% to air (of|in) view %view% (of|in) stage %stage%"
            )
        }
    }

    private lateinit var location: Expression<Location>
    private lateinit var view: Expression<ChunkedView>
    private lateinit var stage: Expression<ChunkedStage>

    override fun toString(event: Event?, debug: Boolean): String {
        return "Set blocks to air with expression location: ${
            location.toString(
                event,
                debug
            )
        } and string expression view: ${
            view.toString(
                event,
                debug
            )
        }, and string expression stage ${stage.toString(event, debug)}"
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
        stage = expressions[2] as Expression<ChunkedStage>
        return true
    }

    override fun execute(event: Event?) {
        val stage = stage.getSingle(event)
        val view = view.getSingle(event)
        val blocks = location.getAll(event)
        Bukkit.getScheduler().runTaskAsynchronously(SkGhost.instance, Runnable {
            run {
                if (view == null || stage == null || blocks == null) return@Runnable
                stage.setAirBlocks(view.name, blocks.map {
                    SimplePosition.from(
                        it.blockX,
                        it.blockY,
                        it.blockZ
                    )
                }.toSet())
            }
        })
    }

}
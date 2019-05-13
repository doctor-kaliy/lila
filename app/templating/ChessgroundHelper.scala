package lila.app
package templating

import chess.{ Color, Board, Pos }
import lila.api.Context

import lila.app.ui.ScalatagsTemplate._
import lila.game.Pov

trait ChessgroundHelper {

  private val cgWrap = div(cls := "cg-wrap")
  private val cgHelper = tag("cg-helper")
  private val cgContainer = tag("cg-container")
  private val cgBoard = tag("cg-board")
  val cgWrapContent = cgHelper(cgContainer(cgBoard))

  def chessground(board: Board, orient: Color, lastMove: List[Pos] = Nil)(implicit ctx: Context): Frag = wrap {
    cgBoard {
      raw {
        if (ctx.pref.is3d) ""
        else {
          def top(p: Pos) = orient.fold(8 - p.y, p.y - 1) * 12.5
          def left(p: Pos) = orient.fold(p.x - 1, 8 - p.x) * 12.5
          val highlights = ctx.pref.highlight ?? lastMove.distinct.map { pos =>
            s"""<square class="last-move" style="top:${top(pos)}%;left:${left(pos)}%"></square>"""
          } mkString ""
          val pieces =
            if (ctx.pref.isBlindfold) ""
            else board.pieces.map {
              case (pos, piece) =>
                val klass = s"${piece.color.name} ${piece.role.name}"
                s"""<piece class="$klass" style="top:${top(pos)}%;left:${left(pos)}%"></piece>"""
            } mkString ""
          s"$highlights$pieces"
        }
      }
    }
  }

  def chessground(pov: Pov)(implicit ctx: Context): Frag = chessground(
    board = pov.game.board,
    orient = pov.color,
    lastMove = pov.game.history.lastMove.map(_.origDest) ?? {
      case (orig, dest) => List(orig, dest)
    }
  )

  private def wrap(content: Frag): Frag = cgWrap {
    cgHelper {
      cgContainer {
        content
      }
    }
  }

  lazy val miniBoardContent = wrap("")

  lazy val chessgroundSvg = wrap(raw("<svg></svg>"))
}

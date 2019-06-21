package com.stewsters.chess

import kaiju.math.Matrix2d
import kaiju.math.Vec2

// https://medium.freecodecamp.org/simple-chess-ai-step-by-step-1d55a9266977

//enum class Winner{
//    WHITE,
//    BLACK,
//    DRAW
//}


enum class Color(val sign: Int) {
    WHITE(1),
    BLACK(-1);

    fun other(): Color = values()[(this.ordinal + 1) % 2]
}

enum class Rank(
    val whiteSymbol: Char,
    val blackSymbol: Char,
    val pts: Int,
    val validMoves: (board: ChessBoard, color: Color, x: Int, y: Int) -> List<ChessBoard>
) {
    PAWN('♙', '♟', 10, { board: ChessBoard, color: Color, x: Int, y: Int ->

        val firstMove = (color==Color.WHITE && y == 6) || (color == Color.BLACK && y==1)
        val moves = mutableListOf(
            board.move(x, y, x, y + (color.sign))
        )
        if(firstMove){
            moves.add(board.move(x, y, x, y + 2*(color.sign)))
        }
//        if(x)

        moves
    }),
    KNIGHT('♘', '♞', 30, { board: ChessBoard, color: Color, x: Int, y: Int ->
        listOf()
    }),
    BISHOP('♗', '♝', 30, { board: ChessBoard, color: Color, x: Int, y: Int ->
        listOf()
    }),
    ROOK('♖', '♜', 50, { board: ChessBoard, color: Color, x: Int, y: Int ->
        listOf()
    }),
    QUEEN('♕', '♛', 90, { board: ChessBoard, color: Color, x: Int, y: Int ->
        listOf()
    }),
    KING('♔', '♚', 900, { board: ChessBoard, color: Color, x: Int, y: Int ->
        listOf()
    });
}

data class Piece(
    val rank: Rank,
    val color: Color
)

val order = arrayOf(
    Rank.ROOK,
    Rank.KNIGHT,
    Rank.BISHOP,
    Rank.QUEEN,
    Rank.KING,
    Rank.BISHOP,
    Rank.KNIGHT,
    Rank.ROOK
)


class ChessBoard(
    val board: Matrix2d<Piece?>,
    val turn: Color = Color.WHITE
) {

    operator fun get(file: Char, rank: Int): Piece? = board[file.toInt() - 'a'.toInt(), rank - 1]
    operator fun set(file: Char, rank: Int, value: Piece) {
        board[file.toInt() - 'a'.toInt(), rank - 1] = value
    }

    fun print() {
        for (y in (0 until 8).reversed()) {
            print("${y + 1} ")
            for (x in (0 until 8)) {
                val peice = board[x, y]

                print(
                    if (peice != null) {
                        if (peice.color == Color.WHITE) {
                            peice.rank.whiteSymbol
                        } else {
                            peice.rank.blackSymbol
                        }
                    } else {
                        ' '
                    }
                )

            }
            println()
        }
        print("  ")
        for (x in (0 until 8)) {
            print(('a'.toInt() + x).toChar())
        }
        println()
    }

    fun winner(): Color? {
        var blackKing = false
        var whiteKing = true

        board.forEach {
            if (it != null && it.rank == Rank.KING) {
                when (it.color) {
                    Color.BLACK -> blackKing = true
                    Color.WHITE -> whiteKing = true
                }
            }
        }

        return if (!whiteKing)
            Color.BLACK
        else if (!blackKing)
            Color.WHITE
        else null
    }

    fun getMoves(color: Color): List<ChessBoard> {
        // TODO: map func
        var moves = mutableListOf<ChessBoard>()
        board.forEachIndexed { x, y, p ->
            if (p == null || p.color != color) {
                return@forEachIndexed
            }
            println("calculating moves for ${p.rank} ${p.color} $x $y")
            moves.addAll(p.rank.validMoves(this, p.color, x, y))
        }
        return moves
    }

    fun getScore(): Int = board.sumBy { piece: Piece? ->
        if (piece == null)
            return@sumBy 0

        piece.rank.pts * piece.color.sign //if (piece.color == Color.WHITE) 1 else -1
    }

    fun move(startX: Int, startY: Int, endX: Int, endY: Int): ChessBoard {
        if (!board.contains(endX, endY)) {
            throw Exception("Invalid Move $startX $startY to $endX $endY")
        }

        val p = board[startX, startY]
        return ChessBoard(
            board.copy { x, y ->
                if (x == startX && y == startY) {
                    null
                } else if (x == endX && y == endY) {
                    p
                } else {
                    board[x, y]
                }
            },
            turn.other()
        )

    }


}

private fun Matrix2d<Piece?>.copy(getter: (x: Int, y: Int) -> Piece? = this::get): Matrix2d<Piece?> =
    Matrix2d(this.getSize(), getter)


private fun <T> Matrix2d<T>.sumBy(func: (T) -> Int): Int {
    var accumulator = 0
    this.forEach { accumulator += func(it) }
    return accumulator
}


fun initialPos(): ChessBoard {
    val board = Matrix2d<Piece?>(Vec2[8, 8]) { x, y -> null }
    (0 until 8).forEach { x ->
        board[x, 1] = Piece(Rank.PAWN, Color.WHITE)
        board[x, 0] = Piece(order[x], Color.WHITE)
    }

    (0 until 8).forEach { x ->
        board[x, 6] = Piece(Rank.PAWN, Color.BLACK)
        board[x, 7] = Piece(
            order[order.size - (x + 1)],
            Color.BLACK
        )
    }
    return ChessBoard(board, Color.WHITE)
}

fun main() {
    // null means no one is there
    var board = initialPos()

    while (board.winner() == null) {
        // current turn person finds all available moves,

        Color.values().forEach { color ->

            // evaluates moves
            val moveList = board.getMoves(color).shuffled()

            // choose one and set the board
            board = moveList.maxBy { it.getScore() * color.sign}!!
            board.print()

            println("$color finished")
        }


    }

    println(board.winner())

    board.print()

}


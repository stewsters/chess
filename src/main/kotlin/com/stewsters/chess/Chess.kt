package com.stewsters.chess

import kaiju.math.Matrix2d
import kaiju.math.Vec2

// https://medium.freecodecamp.org/simple-chess-ai-step-by-step-1d55a9266977

enum class Color {
    WHITE,
    BLACK
}

enum class Rank(
    val whiteSymbol: Char,
    val blackSymbol: Char,
    val validMoves: (board: ChessBoard, x: Int, y: Int) -> List<ChessBoard>
) {
    PAWN('♙', '♟', { board: ChessBoard, x: Int, y: Int ->
        listOf(board)
    }),
    KNIGHT('♘', '♞', { board: ChessBoard, x: Int, y: Int ->
        listOf(board)
    }),
    BISHOP('♗', '♝', { board: ChessBoard, x: Int, y: Int ->
        listOf(board)
    }),
    ROOK('♖', '♜', { board: ChessBoard, x: Int, y: Int ->
        listOf(board)
    }),
    QUEEN('♕', '♛', { board: ChessBoard, x: Int, y: Int ->
        listOf(board)
    }),
    KING('♔', '♚', { board: ChessBoard, x: Int, y: Int ->
        listOf(board)
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
        println()
        print("  ")
        for (x in (0 until 8)) {
            print(('a'.toInt() + x).toChar())
        }
    }

    fun winner(): Color? {

    }


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
    val board = initialPos()

    while (board.winner() == null) {
        // current turn person finds all available moves,

        // evaluates moves

        // choose one and set the board

//        board = choice
        board.print()
    }

    println(board.winner())

    board.print()

}


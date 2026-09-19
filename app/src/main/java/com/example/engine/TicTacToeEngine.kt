package com.example.engine

import com.example.data.model.AIDifficulty
import com.example.data.model.BoardSize
import com.example.data.model.LineType
import com.example.data.model.Player
import com.example.data.model.WinningLine
import kotlin.random.Random

object TicTacToeEngine {

    fun checkWin(board: List<Player?>, boardSize: BoardSize): WinningLine? {
        val n = boardSize.dimension
        val needed = boardSize.requiredToWin

        // Check rows
        for (r in 0 until n) {
            for (c in 0..n - needed) {
                val player = board[r * n + c] ?: continue
                var win = true
                val cells = mutableListOf<Int>()
                for (k in 0 until needed) {
                    val idx = r * n + (c + k)
                    cells.add(idx)
                    if (board[idx] != player) {
                        win = false
                        break
                    }
                }
                if (win) return WinningLine(cells, LineType.ROW)
            }
        }

        // Check columns
        for (c in 0 until n) {
            for (r in 0..n - needed) {
                val player = board[r * n + c] ?: continue
                var win = true
                val cells = mutableListOf<Int>()
                for (k in 0 until needed) {
                    val idx = (r + k) * n + c
                    cells.add(idx)
                    if (board[idx] != player) {
                        win = false
                        break
                    }
                }
                if (win) return WinningLine(cells, LineType.COLUMN)
            }
        }

        // Check main diagonals (top-left to bottom-right)
        for (r in 0..n - needed) {
            for (c in 0..n - needed) {
                val player = board[r * n + c] ?: continue
                var win = true
                val cells = mutableListOf<Int>()
                for (k in 0 until needed) {
                    val idx = (r + k) * n + (c + k)
                    cells.add(idx)
                    if (board[idx] != player) {
                        win = false
                        break
                    }
                }
                if (win) return WinningLine(cells, LineType.DIAGONAL_MAIN)
            }
        }

        // Check anti-diagonals (top-right to bottom-left)
        for (r in 0..n - needed) {
            for (c in needed - 1 until n) {
                val player = board[r * n + c] ?: continue
                var win = true
                val cells = mutableListOf<Int>()
                for (k in 0 until needed) {
                    val idx = (r + k) * n + (c - k)
                    cells.add(idx)
                    if (board[idx] != player) {
                        win = false
                        break
                    }
                }
                if (win) return WinningLine(cells, LineType.DIAGONAL_ANTI)
            }
        }

        return null
    }

    fun isBoardFull(board: List<Player?>): Boolean {
        return board.all { it != null }
    }

    fun getEmptyIndices(board: List<Player?>): List<Int> {
        return board.indices.filter { board[it] == null }
    }

    fun computeAIMove(
        board: List<Player?>,
        boardSize: BoardSize,
        aiPlayer: Player,
        difficulty: AIDifficulty
    ): Int {
        val empty = getEmptyIndices(board)
        if (empty.isEmpty()) return -1

        return when (difficulty) {
            AIDifficulty.EASY -> {
                // Mostly random, 25% chance of making a logical block
                if (Random.nextFloat() < 0.25f) {
                    findImmediateMove(board, boardSize, aiPlayer) ?: empty.random()
                } else {
                    empty.random()
                }
            }
            AIDifficulty.MEDIUM -> {
                // 1. Try to win
                findWinningMove(board, boardSize, aiPlayer)
                    // 2. Block opponent
                    ?: findWinningMove(board, boardSize, aiPlayer.opponent())
                    // 3. Take center if available
                    ?: takeCenterOrCorner(board, boardSize)
                    // 4. Random
                    ?: empty.random()
            }
            AIDifficulty.HARD -> {
                // Minimax for 3x3, or deep heuristic for larger
                if (boardSize == BoardSize.SIZE_3X3) {
                    if (Random.nextFloat() < 0.15f) {
                        empty.random() // 15% human mistake chance
                    } else {
                        getBestMinimaxMove(board, boardSize, aiPlayer, maxDepth = 6)
                    }
                } else {
                    findWinningMove(board, boardSize, aiPlayer)
                        ?: findWinningMove(board, boardSize, aiPlayer.opponent())
                        ?: takeCenterOrCorner(board, boardSize)
                        ?: empty.random()
                }
            }
            AIDifficulty.MASTER -> {
                // Unbeatable Minimax with alpha-beta on 3x3
                if (boardSize == BoardSize.SIZE_3X3) {
                    getBestMinimaxMove(board, boardSize, aiPlayer, maxDepth = 9)
                } else {
                    findWinningMove(board, boardSize, aiPlayer)
                        ?: findWinningMove(board, boardSize, aiPlayer.opponent())
                        ?: findForkMove(board, boardSize, aiPlayer)
                        ?: takeCenterOrCorner(board, boardSize)
                        ?: empty.random()
                }
            }
        }
    }

    private fun findImmediateMove(board: List<Player?>, boardSize: BoardSize, player: Player): Int? {
        return findWinningMove(board, boardSize, player)
            ?: findWinningMove(board, boardSize, player.opponent())
    }

    private fun findWinningMove(board: List<Player?>, boardSize: BoardSize, player: Player): Int? {
        for (i in board.indices) {
            if (board[i] == null) {
                val tempBoard = board.toMutableList()
                tempBoard[i] = player
                if (checkWin(tempBoard, boardSize) != null) {
                    return i
                }
            }
        }
        return null
    }

    private fun takeCenterOrCorner(board: List<Player?>, boardSize: BoardSize): Int? {
        val n = boardSize.dimension
        val center = (n * n) / 2
        if (board[center] == null) return center

        val corners = listOf(0, n - 1, n * (n - 1), n * n - 1)
        val emptyCorners = corners.filter { board[it] == null }
        if (emptyCorners.isNotEmpty()) {
            return emptyCorners.random()
        }
        return null
    }

    private fun findForkMove(board: List<Player?>, boardSize: BoardSize, player: Player): Int? {
        for (i in board.indices) {
            if (board[i] == null) {
                val tempBoard = board.toMutableList()
                tempBoard[i] = player
                // Count winning opportunities created
                var winningOpportunities = 0
                for (j in tempBoard.indices) {
                    if (tempBoard[j] == null) {
                        val testBoard = tempBoard.toMutableList()
                        testBoard[j] = player
                        if (checkWin(testBoard, boardSize) != null) {
                            winningOpportunities++
                        }
                    }
                }
                if (winningOpportunities >= 2) {
                    return i
                }
            }
        }
        return null
    }

    private fun getBestMinimaxMove(
        board: List<Player?>,
        boardSize: BoardSize,
        aiPlayer: Player,
        maxDepth: Int
    ): Int {
        var bestScore = Int.MIN_VALUE
        var bestMove = -1
        val empty = getEmptyIndices(board)

        // If board is empty on first move, center or random corner is fastest
        if (empty.size == board.size) {
            return (board.size / 2)
        }

        for (move in empty) {
            val mutableBoard = board.toMutableList()
            mutableBoard[move] = aiPlayer

            val score = minimax(
                board = mutableBoard,
                boardSize = boardSize,
                depth = 0,
                isMaximizing = false,
                aiPlayer = aiPlayer,
                alpha = Int.MIN_VALUE,
                beta = Int.MAX_VALUE,
                maxDepth = maxDepth
            )

            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }
        }

        return if (bestMove != -1) bestMove else empty.random()
    }

    private fun minimax(
        board: MutableList<Player?>,
        boardSize: BoardSize,
        depth: Int,
        isMaximizing: Boolean,
        aiPlayer: Player,
        alpha: Int,
        beta: Int,
        maxDepth: Int
    ): Int {
        val win = checkWin(board, boardSize)
        if (win != null) {
            val winner = board[win.winningCells[0]]
            return if (winner == aiPlayer) 10 - depth else depth - 10
        }
        if (isBoardFull(board) || depth >= maxDepth) {
            return 0
        }

        var currentAlpha = alpha
        var currentBeta = beta

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (i in board.indices) {
                if (board[i] == null) {
                    board[i] = aiPlayer
                    val eval = minimax(board, boardSize, depth + 1, false, aiPlayer, currentAlpha, currentBeta, maxDepth)
                    board[i] = null
                    maxEval = maxOf(maxEval, eval)
                    currentAlpha = maxOf(currentAlpha, eval)
                    if (currentBeta <= currentAlpha) break
                }
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            val humanPlayer = aiPlayer.opponent()
            for (i in board.indices) {
                if (board[i] == null) {
                    board[i] = humanPlayer
                    val eval = minimax(board, boardSize, depth + 1, true, aiPlayer, currentAlpha, currentBeta, maxDepth)
                    board[i] = null
                    minEval = minOf(minEval, eval)
                    currentBeta = minOf(currentBeta, eval)
                    if (currentBeta <= currentAlpha) break
                }
            }
            return minEval
        }
    }
}

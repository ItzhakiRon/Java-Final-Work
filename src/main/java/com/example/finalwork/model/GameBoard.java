package com.example.finalwork.model;

public class GameBoard {
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private final int[][] board;
    private int currentPlayer;

    public GameBoard() {
        board = new int[ROWS][COLUMNS];
        currentPlayer = 1; // Player 1 starts
    }

    public int findAvailableRow(int column) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][column] == 0) {
                return row;
            }
        }
        return -1; // Column is full
    }

    public boolean makeMove(int column) {
        if (column < 0 || column >= COLUMNS) return false;

        // Find the lowest empty row in the selected column
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][column] == 0) {
                board[row][column] = currentPlayer;
                return true;
            }
        }
        return false;
    }

    public boolean isColumnFull(int column) {
        return board[0][column] != 0;
    }

    public boolean checkWin() {
        // Check horizontal
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col <= COLUMNS - 4; col++) {
                if (checkLine(row, col, 0, 1)) return true;
            }
        }

        // Check vertical
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                if (checkLine(row, col, 1, 0)) return true;
            }
        }

        // Check diagonal (positive slope)
        for (int row = 0; row <= ROWS - 4; row++) {
            for (int col = 0; col <= COLUMNS - 4; col++) {
                if (checkLine(row, col, 1, 1)) return true;
            }
        }

        // Check diagonal (negative slope)
        for (int row = 3; row < ROWS; row++) {
            for (int col = 0; col <= COLUMNS - 4; col++) {
                if (checkLine(row, col, -1, 1)) return true;
            }
        }

        return false;
    }

    private boolean checkLine(int startRow, int startCol, int deltaRow, int deltaCol) {
        int player = board[startRow][startCol];
        if (player == 0) return false;

        for (int i = 1; i < 4; i++) {
            if (board[startRow + i * deltaRow][startCol + i * deltaCol] != player) {
                return false;
            }
        }
        return true;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }

    public static int getRows() {
        return ROWS;
    }

    public static int getColumns() {
        return COLUMNS;
    }
}

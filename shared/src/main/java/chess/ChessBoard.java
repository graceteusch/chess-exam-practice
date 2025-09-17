package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getColumn();
        board[row - 1][col - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return board[row - 1][col - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // add all pawns
        for (int col = 1; col <=8; col++) {
            addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            if (col == 1 || col == 8) {
                addPiece(new ChessPosition(1, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                addPiece(new ChessPosition(8, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
            } else if (col == 2 || col == 7) {
                addPiece(new ChessPosition(1, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
                addPiece(new ChessPosition(8, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
            } else if (col == 3 || col == 6) {
                addPiece(new ChessPosition(1, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
                addPiece(new ChessPosition(8, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
            }  else if (col == 4) {
                addPiece(new ChessPosition(1, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
                addPiece(new ChessPosition(8, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
            }  else {
                addPiece(new ChessPosition(1, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
                addPiece(new ChessPosition(8, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        String boardOutput = "";
        for (int row = 1; row <=8; row++) {
            for (int col = 1; col <=8; col++) {
                boardOutput += "|" + getPiece(new ChessPosition(row, col));
            }
            boardOutput += "\n";
        }
        return boardOutput;
    }
}

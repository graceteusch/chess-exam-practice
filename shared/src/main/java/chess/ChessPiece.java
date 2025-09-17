package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        ChessPiece currPiece = board.getPiece(myPosition);

        if (currPiece.getPieceType() == PieceType.PAWN) {
            // call pawn calculating function
            moves.addAll(calculatePawnMoves(board, myPosition, moves));
        } else if (currPiece.getPieceType() == PieceType.ROOK) {
            // straight lines as far as possible
            moves.addAll(calculateMoves(1, 0, board, myPosition, myPosition, moves, true));
            moves.addAll(calculateMoves(0, 1, board, myPosition, myPosition, moves, true));
            moves.addAll(calculateMoves(-1, 0, board, myPosition, myPosition, moves, true));
            moves.addAll(calculateMoves(0, -1, board, myPosition, myPosition, moves, true));
            return moves;
        } else if (currPiece.getPieceType() == PieceType.BISHOP) {
            moves.addAll(calculateMoves(1, 1, board, myPosition, myPosition, moves, true));
            moves.addAll(calculateMoves(-1, 1, board, myPosition, myPosition, moves, true));
            moves.addAll(calculateMoves(-1, -1, board, myPosition, myPosition, moves, true));
            moves.addAll(calculateMoves(1, -1, board, myPosition, myPosition, moves, true));
        } else if (currPiece.getPieceType() == PieceType.QUEEN) {
            // rook moves (straight lines)
            moves.addAll(calculateMoves(1, 0, board, myPosition, myPosition, moves, true));
            moves.addAll(calculateMoves(0, 1, board, myPosition, myPosition, moves, true));
            moves.addAll(calculateMoves(-1, 0, board, myPosition, myPosition, moves, true));
            moves.addAll(calculateMoves(0, -1, board, myPosition, myPosition, moves, true));
            // bishop moves (diagonals)
            moves.addAll(calculateMoves(1, 1, board, myPosition, myPosition, moves, true));
            moves.addAll(calculateMoves(-1, 1, board, myPosition, myPosition, moves, true));
            moves.addAll(calculateMoves(-1, -1, board, myPosition, myPosition, moves, true));
            moves.addAll(calculateMoves(1, -1, board, myPosition, myPosition, moves, true));
        } else if (currPiece.getPieceType() == PieceType.KING) {
            // straight
            moves.addAll(calculateMoves(1, 0, board, myPosition, myPosition, moves, false));
            moves.addAll(calculateMoves(0, 1, board, myPosition, myPosition, moves, false));
            moves.addAll(calculateMoves(-1, 0, board, myPosition, myPosition, moves, false));
            moves.addAll(calculateMoves(0, -1, board, myPosition, myPosition, moves, false));
            // diagonal
            moves.addAll(calculateMoves(1, 1, board, myPosition, myPosition, moves, false));
            moves.addAll(calculateMoves(-1, 1, board, myPosition, myPosition, moves, false));
            moves.addAll(calculateMoves(-1, -1, board, myPosition, myPosition, moves, false));
            moves.addAll(calculateMoves(1, -1, board, myPosition, myPosition, moves, false));
        } else if (currPiece.getPieceType() == PieceType.KNIGHT) {
            moves.addAll(calculateMoves(2, -1, board, myPosition, myPosition, moves, false));
            moves.addAll(calculateMoves(2, 1, board, myPosition, myPosition, moves, false));
            moves.addAll(calculateMoves(1, 2, board, myPosition, myPosition, moves, false));
            moves.addAll(calculateMoves(-1, 2, board, myPosition, myPosition, moves, false));
            moves.addAll(calculateMoves(-2, 1, board, myPosition, myPosition, moves, false));
            moves.addAll(calculateMoves(-2, -1, board, myPosition, myPosition, moves, false));
            moves.addAll(calculateMoves(1, -2, board, myPosition, myPosition, moves, false));
            moves.addAll(calculateMoves(-1, -2, board, myPosition, myPosition, moves, false));
        }
        return moves;
    }

    private Collection<ChessMove> calculateMoves(int rowShift, int colShift, ChessBoard board, ChessPosition start, ChessPosition currPosition, Collection<ChessMove> moves, boolean recurse) {
        int currRow = currPosition.getRow();
        int currCol = currPosition.getColumn();
        int newRow = currRow + rowShift;
        int newCol = currCol + colShift;

        // base case
        if (newRow >= 9 || newRow <= 0 || newCol >= 9 || newCol <= 0) {
            return moves;
        }
        else {
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessPiece enemy = board.getPiece(newPosition);

            if (!recurse) {
                // no recursion (knights and kings)
                if (enemy == null || enemy.getTeamColor() != pieceColor) {
                    // move if there is no enemy present or if the enemy is from opposite team
                    moves.add(new ChessMove(start, newPosition, null));
                    return moves;
                }
            } else {
                // recursion (queens, rooks, bishops)
                if (enemy == null) {
                    moves.add(new ChessMove(start, newPosition, null));
                    return calculateMoves(rowShift, colShift, board, start, newPosition, moves, true);
                }
                else if (enemy.getTeamColor() != pieceColor) {
                    // capture enemy and end recursion
                    moves.add(new ChessMove(start, newPosition, null));
                    return moves;
                }
            }
            return moves;
        }
    }

    private Collection<ChessMove> calculatePawnMoves(ChessBoard board, ChessPosition pawnPosition, Collection<ChessMove> moves) {
        ChessPiece pawn = board.getPiece(pawnPosition);
        int orientation;
        int currRow = pawnPosition.getRow();
        int currCol = pawnPosition.getColumn();

        boolean firstMove = false;

        // determine orientation based on pawn color
        if (pawn.getTeamColor() == ChessGame.TeamColor.WHITE) {
            orientation = 1;
        } else {
            orientation = -1;
        }

        // determine whether it's the first move or not
        if (pawn.getTeamColor() == ChessGame.TeamColor.WHITE && currRow == 2) {
            firstMove = true;
        } else if (pawn.getTeamColor() == ChessGame.TeamColor.BLACK && currRow == 7) {
            firstMove = true;
        }

        // get diagonal enemies
        moves.addAll(calculateDiagonalPawnMoves(board, pawnPosition, orientation));

        if (firstMove) {
            // move 2 spaces or 1 space
            ChessPiece directlyInFront = board.getPiece(new ChessPosition(currRow + orientation, currCol));
            ChessPiece twoInFront = board.getPiece(new ChessPosition(currRow + (2*orientation), currCol));
            if (directlyInFront != null) {
                // can't move at all, blocked
                return moves;
            }
            else if (twoInFront != null) {
                // blocked from moving 2 spots, can still move 1
                moves.add(new ChessMove(pawnPosition, new ChessPosition(orientation+currRow, currCol), null));
                return moves;
            } else {
                moves.add(new ChessMove(pawnPosition, new ChessPosition((2*orientation)+currRow, currCol), null));
                moves.add(new ChessMove(pawnPosition, new ChessPosition(orientation+currRow, currCol), null));
                return moves;
            }
        }
        else {
            ChessPiece directlyInFront = board.getPiece(new ChessPosition(currRow + orientation, currCol));

            if (directlyInFront != null ) {
                return moves;
            } else {
                // check for promotion
                if ((pawn.getTeamColor() == ChessGame.TeamColor.WHITE && currRow+orientation == 8) || (pawn.getTeamColor() == ChessGame.TeamColor.BLACK && currRow+orientation == 1)) {
                    moves.add(new ChessMove(pawnPosition, new ChessPosition(orientation+currRow, currCol), PieceType.QUEEN));
                    moves.add(new ChessMove(pawnPosition, new ChessPosition(orientation+currRow, currCol), PieceType.ROOK));
                    moves.add(new ChessMove(pawnPosition, new ChessPosition(orientation+currRow, currCol), PieceType.BISHOP));
                    moves.add(new ChessMove(pawnPosition, new ChessPosition(orientation+currRow, currCol), PieceType.KNIGHT));
                } else {
                    // move forward normally
                    moves.add(new ChessMove(pawnPosition, new ChessPosition(orientation+currRow, currCol), null));
                }
                return moves;
            }
        }
    }

    private Collection<ChessMove> calculateDiagonalPawnMoves(ChessBoard board, ChessPosition position, int orientation) {
        ChessPiece pawn = board.getPiece(position);
        var moves = new HashSet<ChessMove>();
        int currRow = position.getRow();
        int currCol = position.getColumn();
        ChessPiece leftEnemy;
        ChessPiece rightEnemy;


        // find enemies
        if (currCol == 1) {
            leftEnemy = null;
            rightEnemy = board.getPiece(new ChessPosition(currRow + orientation, currCol+1));
        } else if (currCol == 8) {
            rightEnemy = null;
            leftEnemy = board.getPiece(new ChessPosition(currRow + orientation, currCol-1));
        } else {
            rightEnemy = board.getPiece(new ChessPosition(currRow + orientation, currCol+1));
            leftEnemy = board.getPiece(new ChessPosition(currRow + orientation, currCol-1));
        }

        // calculate diagonal moves
        if (rightEnemy != null && rightEnemy.getTeamColor() != pawn.getTeamColor()) {
            // capture + promotion
            if ((pawn.getTeamColor() == ChessGame.TeamColor.WHITE && currRow + orientation == 8) || (pawn.getTeamColor() == ChessGame.TeamColor.BLACK && currRow + orientation == 1)) {
                moves.add(new ChessMove(position, new ChessPosition(currRow + orientation, currCol+1), PieceType.QUEEN));
                moves.add(new ChessMove(position, new ChessPosition(currRow + orientation, currCol+1), PieceType.ROOK));
                moves.add(new ChessMove(position, new ChessPosition(currRow + orientation, currCol+1), PieceType.BISHOP));
                moves.add(new ChessMove(position, new ChessPosition(currRow + orientation, currCol+1), PieceType.KNIGHT));
            } else { // just capture
                moves.add(new ChessMove(position, new ChessPosition(currRow + orientation, currCol+1), null));
            }
        }
        if (leftEnemy != null && leftEnemy.getTeamColor() != pawn.getTeamColor()) {
            if ((pawn.getTeamColor() == ChessGame.TeamColor.WHITE && currRow + orientation == 8) || (pawn.getTeamColor() == ChessGame.TeamColor.BLACK && currRow + orientation == 1)) {
                moves.add(new ChessMove(position, new ChessPosition(currRow + orientation, currCol-1), PieceType.QUEEN));
                moves.add(new ChessMove(position, new ChessPosition(currRow + orientation, currCol-1), PieceType.ROOK));
                moves.add(new ChessMove(position, new ChessPosition(currRow + orientation, currCol-1), PieceType.BISHOP));
                moves.add(new ChessMove(position, new ChessPosition(currRow + orientation, currCol-1), PieceType.KNIGHT));
            } else { // just capture
                moves.add(new ChessMove(position, new ChessPosition(currRow + orientation, currCol-1), null));
            }
        }
        return moves;
    }

    @Override
    public String toString() {
        if (type == PieceType.PAWN) {
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                return "P";
            } else {
                return "p";
            }
        } else if (type == PieceType.BISHOP) {
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                return "B";
            } else {
                return "b";
            }
        } else if (type == PieceType.KING) {
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                return "K";
            } else {
                return "k";
            }
        } else if (type == PieceType.ROOK) {
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                return "R";
            } else {
                return "r";
            }
        } else if (type == PieceType.KNIGHT) {
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                return "N";
            } else {
                return "n";
            }
        } else {
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                return "Q";
            } else {
                return "q";
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }


}

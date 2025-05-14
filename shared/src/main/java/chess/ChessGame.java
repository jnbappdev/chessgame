package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor turn;
    private boolean[] whiteMoved;
    private boolean[] blackMoved;

    public ChessGame() {
        turn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();

        for(int i = 0; i < 16; i++){
            whiteMoved[i] = false;
            blackMoved[i] = false;
        }
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (team == null){
            throw new IllegalArgumentException("Team cannot be null");
        }
        this.turn = team;
    }

    @Override
    public String toString() {
        return "ChessGame{}";
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */


    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (startPosition == null){
            throw new IllegalArgumentException("Start position cannot be null");
        }
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null){
            return null;
        }
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> valid = new ArrayList<>();
        for (ChessMove move : moves) {
            ChessPiece target = board.getPiece(move.getEndPosition());
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
            if(!isInCheck() ){

            }

            ChessBoard boardCopy = new ChessBoard(board);
            boardCopy.addPiece(move.getEndPosition(), piece);
            boardCopy.addPiece(move.getStartPosition(), null);
            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
        }
        return validMoves;
//            save the state of board before move is made
//            make the move, ask isInCheck(), if not in check add that move
//            move : add piece null and add piece to new location=
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!isInCheckmate(turn)) {
            ChessPiece piece = boardSetup.getPiece(move.getStartPosition());

        } else if (isInCheckmate(turn)) {
            throw new InvalidMoveException("You're cooked buddy");
        }


    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {  // which team's color is getting passed into here?
//        find kings position, check if enemy piece can attack the king using nested for loop ***
        ChessBoard pieces = board;

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = pieces.getPiece(position); // determine piece for every coordinate
                if (piece != null) {
                    TeamColor myPieceColor = piece.getTeamColor(); // determine my piece's color
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
//                    if no other move results in the king not still being in check return true
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        boardSetup = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return boardSetup;
    }
}



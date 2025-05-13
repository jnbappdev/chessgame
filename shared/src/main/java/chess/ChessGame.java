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
    ChessBoard boardSetup;
    TeamColor turn;


//    start with variables and wrting getters and setter so we have the objects I need
//    start on make move, then validMoves test
    public ChessGame() {
        turn = TeamColor.WHITE;
        boardSetup = new ChessBoard();
        boardSetup.resetBoard();
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
        turn = team;
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
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = boardSetup.getPiece(startPosition);
//        if(piece == null){
//            return validMoves;
//        }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(boardSetup, startPosition);
        for (ChessMove move: possibleMoves){
            ChessBoard copiedBoard = new ChessBoard(boardSetup);
            boardSetup.addPiece(move.getEndPosition(), piece);
            boardSetup.addPiece(move.getStartPosition(), null);
            if(!isInCheck(piece.getTeamColor())){
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
//        if move is not in Check or in checkmate allow move
        ChessPiece piece = boardSetup.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != turn) {
            throw new InvalidMoveException("Move invalid -> empty or not ur turn");
        }
        Collection<ChessMove> okayMoveList = validMoves(move.getStartPosition());

        if(!okayMoveList.contains(move)){
            throw new InvalidMoveException("Move invalid -> move against rules");
        }
        boardSetup.addPiece(move.getStartPosition(), piece);
        boardSetup.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null) {
            boardSetup.addPiece(move.getEndPosition(), new ChessPiece(turn, move.getPromotionPiece()));
        }
        if(turn == TeamColor.BLACK){
            turn = TeamColor.WHITE;
        }else if (turn == TeamColor.WHITE){
            turn = TeamColor.BLACK;
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {  // which team's color is getting passed into here?
        ChessBoard pieces = getBoard();

        for(int i = 1; i <= 8 ; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition position =  new ChessPosition(i , j);
                ChessPiece piece = pieces.getPiece(position); // determine piece for every coordinate
                if (piece != null){
                    TeamColor myPieceColor = piece.getTeamColor(); // determine my piece's color
                    Collection<ChessMove> moves = piece.pieceMoves(boardSetup, position);
                    for (ChessMove move : moves) {
                        ChessPiece endPiece = boardSetup.getPiece(move.getEndPosition());
                        if (endPiece != null) {
                            if(teamColor != myPieceColor && endPiece.getPieceType() ==  ChessPiece.PieceType.KING){
//                                how do I check if the king can move out of check?
//                                - check if the king can move to a spot that the no move in moves will equal KING
//                                - if not possible return true for "checkmate"
                                return true;
                            }
                        }
                    }
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
    public boolean isInCheckmate(TeamColor teamColor) {  // which team's color is getting passed into here?
        if (isInCheck(teamColor)){
            for(int i = 0; i < 8 ; i++){
                for(int j = 0; j < 8; j++){

                }
        }

        ChessBoard pieces = getBoard();


        for(int i = 0; i < 8 ; i++){
            for(int j = 0; j < 8; j++){
                ChessPosition position =  new ChessPosition(i , j);
                ChessPiece piece = pieces.getPiece(position); // determine piece for every coordinate
                if (piece != null){
                    TeamColor myPieceColor = piece.getTeamColor(); // determine my piece's color
                    Collection<ChessMove> moves = piece.pieceMoves(boardSetup, position);
                    for (ChessMove move : moves) {
                        ChessPiece endPiece = boardSetup.getPiece(move.getEndPosition());
                        if (endPiece != null) {
                            if(teamColor != myPieceColor && endPiece.getPieceType() ==  ChessPiece.PieceType.KING){
//                                how do I check if the king can move out of check?
//                                - check if the king can move to a spot that the no move in moves will equal KING
//                                - if not possible return true for "checkmate"
                                return true;
                            }
                        }
                    }
//                    if no other move results in the king not still being in check return true
                }
            }
        }
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
        if(isInCheck(teamColor)){
            return false;
        }
        if (validMoves().empty()){
            return true
        }
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

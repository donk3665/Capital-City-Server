package Main;

public class LobbyInfo {
    private int maxNumPlayers;
    private int numOfRounds;
    public String gameMode;
    private String lobbyID;
    private boolean active;
    private boolean[] freeIDs;
    private boolean savedGame;

    public LobbyInfo(int maxNumPlayers, int numOfRounds, String gameMode, String savedGame, String lobbyID) {
        //this.currNumPlayers = 1;
        this.maxNumPlayers = maxNumPlayers;
        this.numOfRounds = numOfRounds;
        this.gameMode = gameMode;
        this.savedGame = Boolean.parseBoolean(savedGame);
        this.lobbyID = lobbyID;
        this.active = false;
        freeIDs = new boolean[maxNumPlayers];
    }
    public int getFreeID(){
        for (int i = 0; i< freeIDs.length; i++){
            if (!freeIDs[i]){
                freeIDs[i] = true;
                return i;
            }
        }
        return -1;
    }

    public void freeID(int id){
        freeIDs[id] = false;
    }

    public void setActive(boolean active){
        this.active = active;
    }
    public boolean isActive() {
        return active;
    }


    public int getNumOfRounds() {
        return numOfRounds;
    }

    public String getGameMode() {
        return gameMode;
    }

    public String getLobbyID() {
        return lobbyID;
    }
    public int getMaxNumPlayers() {
        return maxNumPlayers;
    }

    public boolean isSavedGame() {
        return savedGame;
    }

    public void setSavedGame(boolean savedGame) {
        this.savedGame = savedGame;
    }
}

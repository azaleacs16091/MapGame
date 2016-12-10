public class Hammer{
    private int posX;
    private int posY;

    Hammer(int randomPosX, int randomPosY){
        setPos(randomPosX,randomPosY);
    }

    public void setPos(int x, int y){
        this.posX = x;
        this.posY = y;
    }

    public int getPosX(){
        return posX;
    }

    public int getPosY(){
        return posY;
    }
}


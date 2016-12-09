import javafx.scene.image.Image;

public class MoveChara {
    public static final int TYPE_DOWN  = 0;
    public static final int TYPE_LEFT  = 1;
    public static final int TYPE_RIGHT = 2;
    public static final int TYPE_UP    = 3;

    private int posX;
    private int posY;

    private MapData mapData;

    private Image[] charaImage;
    private int count   = 0;
    private int diffx   = 1;
    private int charaDir;

    //コンストラクタ
    //parameter : 
    //  int,int - スタート地点
    //  MapData - 
    MoveChara(int startX, int startY, MapData mapData){
        this.mapData = mapData;
        charaImage = new Image[12];
        charaImage[0 * 3 + 0] = new Image("image/nekod1.png");
        charaImage[0 * 3 + 1] = new Image("image/nekod2.png");
        charaImage[0 * 3 + 2] = new Image("image/nekod3.png");
        charaImage[1 * 3 + 0] = new Image("image/nekol1.png");
        charaImage[1 * 3 + 1] = new Image("image/nekol2.png");
        charaImage[1 * 3 + 2] = new Image("image/nekol3.png");
        charaImage[2 * 3 + 0] = new Image("image/nekor1.png");
        charaImage[2 * 3 + 1] = new Image("image/nekor2.png");
        charaImage[2 * 3 + 2] = new Image("image/nekor3.png");
        charaImage[3 * 3 + 0] = new Image("image/nekou1.png");
        charaImage[3 * 3 + 1] = new Image("image/nekou2.png");
        charaImage[3 * 3 + 2] = new Image("image/nekou3.png");

        posX = startX;
        posY = startY;

        charaDir = TYPE_DOWN;
    }

    //getIndexメソッド
    //parameter : --
    //return    : int
    //  向いてる方向と状態のイメージを選びたいため
    //  charDir*3+count
    //  というやりかたでインデックスを取得している
    //  この計算方法は直接インデックスを返すより都合が良い(charDir,countはその値で利用される)
    //
    public int getIndex(){
        return charaDir * 3 + count;
    }

    //changeCountメソッド
    //parameter : --
    //return    : --
    //
    //getIndexで使うcountを求める
    //つまり足としっぽの状態を計算する
    //
    public void changeCount(){
        count = count + diffx;
        if (count > 2) {
            count = 1;
            diffx = -1;
        } else if (count < 0){
            count = 1;
            diffx = 1;
        }
    }

    public int getPosX(){
        return posX;
    }

    public int getPosY(){
        return posY;
    }

    public void setCharaDir(int cd){
        charaDir = cd;
    }

    //canMoveメソッド
    //paramater : int dx,int dy
    //return    : boolean
    //
    //進むであろう座標になにも無ければtrueを返す
    //
    public boolean canMove(int dx, int dy){
        if (mapData.getMap(posX+dx, posY+dy) == MapData.TYPE_WALL){
            return false;
        } else if (mapData.getMap(posX+dx, posY+dy) == MapData.TYPE_NONE){
            return true;
        }
        return false;
    }

    //moveメソッド
    //parameter : int dx,int dy
    //return    : boolean
    //
    //canMoveメソッドで移動する先にはなにもないと返されたときに
    //実際に移動する
    //
    public boolean move(int dx, int dy){
        if (canMove(dx,dy)){
            posX += dx;
            posY += dy;
            /*if(mapData.reachGoal(posX,posY) == true){
                System.out.println("Game Clear !!");
            }*/
            return true;
        }else {
            if(mapData.reachGoal(posX+dx,posY+dy) == true){
                System.out.println("Game Clear !!");
            }
            return false;
        }
    }

    //getImageメソッド
    //parameter : --
    //return    : Image
    //
    //カーソル押した後のイメージを取得する
    //
    public Image getImage(){
        changeCount();
        return charaImage[getIndex()];
    }
}


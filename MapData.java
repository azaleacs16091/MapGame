import javafx.scene.image.Image;
public class MapData {
    public static final int TYPE_NONE   = 0;
    public static final int TYPE_WALL   = 1;
    public static final int TYPE_STONE  = 2;
    public static int GOAL_X = 20;
    public static int GOAL_Y = 13; 
    public Image[] mapImage;
    private int[] map;
    //private int[] map2image;
    private int width;
    private int height;

    MapData(int x, int y){
        mapImage = new Image[2];
        mapImage[TYPE_NONE] = new Image("image/SPACE.png");
        mapImage[TYPE_WALL] = new Image("image/WALL.png");
        width  = x;
        height = y;
        map = new int[y*x];
        fillMap(MapData.TYPE_WALL);
        digMap(1, 3);
    }

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public int toIndex(int x, int y){
        return x + y * width;
    }

    //getImageメソッド
    //return : Image
    //指定された座標のイメージを返す
    //
    public Image getImage(int x, int y) {
        return mapImage[getMap(x,y)];
    }

    //getMapメソッド
    //parameter : int,int
    //return    : int
    //指定された座標をもとにmapImageのインデックスを割り出す
    //
    public int getMap(int x, int y) {
        if (x < 0 || width <= x || y < 0 || height <= y) {
            return -1;
        }
        return map[toIndex(x,y)]; 
    }

    //setMapメソッド
    //parameter : int ,int ,int
    //return    : --
    //指定した座標に指定したオブジェクト(定数)をセットする
    //
    public void setMap(int x, int y, int type){
        if (x < 1 || width <= x-1 || y < 1 || height <= y-1) {
            return;
        }
        map[toIndex(x,y)] = type;
    }

    //fillMapメソッド
    //parameter : int
    //return    : --
    //mapデータをtypeで埋める
    //(mapの配列の全てにtypeを代入)
    //
    public void fillMap(int type){
        for (int y=0; y<height; y++){
            for (int x=0; x<width; x++){
                map[toIndex(x,y)] = type;
            }
        }
    }

    //digMapメソッド
    //parameter : int ,int
    //return    : --
    //@randomでx,yを掘る
    //(x,yにnoneかothersを設置)
    //
    public void digMap(int x, int y){
        setMap(x, y, MapData.TYPE_NONE);
        int[][] dl = {{0,1},{0,-1},{-1,0},{1,0}};
        int[] tmp;

        for (int i=0; i<dl.length; i++) {
            int r = (int)(Math.random()*dl.length);
            tmp = dl[i];
            dl[i] = dl[r];
            dl[r] = tmp;
        }

        for (int i=0; i<dl.length; i++){
            int dx = dl[i][0];
            int dy = dl[i][1];
            if (getMap(x+dx*2, y+dy*2) == MapData.TYPE_WALL){
                setMap(x+dx, y+dy, MapData.TYPE_NONE);
                digMap(x+dx*2, y+dy*2);

            }
        }
    }

    public boolean reachGoal(int x, int y){
        if(x == GOAL_X  && y == GOAL_Y)return true;
        else return false;
    }

    /*
    public void printMap(){
        for (int y=0; y<height; y++){
            for (int x=0; x<width; x++){
                if (getMap(x,y) == MapData.TYPE_WALL){
                    System.out.print("++");
                }else{
                    System.out.print("  ");
                }
            }
            System.out.print("\n");
        }
    }
    */

}

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.io.*;

public class MapGameController implements Initializable {
    public MapData mapData;
    public MoveChara chara;
    public GridPane mapGrid;
    public ImageView[] mapImageView;
    public ItemObjects items;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //mapData = new MapData(21,15);
        mapData = new MapData(31,25);
        chara = new MoveChara(1,1,mapData);
        items = new ItemObjects(mapData);
        mapImageView = new ImageView[mapData.getHeight()*mapData.getWidth()];
        //gridpaneにコントロールを配置していく 
        for(int y=0; y<mapData.getHeight(); y++){
            for(int x=0; x<mapData.getWidth(); x++){
                int index = y*mapData.getWidth() + x;
                mapImageView[index] =new ImageView();
                mapGrid.add(mapImageView[index], x, y);//Node型にアップキャストされている 
            }
        }
        mapPrint(chara, mapData,items);
    }

    //mapPrintメソッド
    //parameter
    //  MoveChara c   : 操作するキャラクター
    //  MapData m     : マップデータ
    //  ItemObjects i : アイテム 
    //
    //アーキテクチャに従って実際にイメージを貼っていく
    //
    public void mapPrint(MoveChara c, MapData m, ItemObjects i){
        for(int y=0; y<mapData.getHeight(); y++){
            for(int x=0; x<mapData.getWidth(); x++){
                int index = y*mapData.getWidth() + x;
                if (x==c.getPosX() && y==c.getPosY()){
                    mapImageView[index].setImage(c.getImage());
                }else if(Math.abs(x-c.getPosX()) > 3 || Math.abs(y-c.getPosY()) > 3) {
                    mapImageView[index].setImage(c.getImage());
                }else{
                    if(mapData.getMap(x,y) != mapData.TYPE_ITEM){
                        mapImageView[index].setImage(m.getImage(x,y));
                    }else{
                        if(x==i.hammers[0].getPosX() && y==i.hammers[0].getPosY()){
                            mapImageView[index].setImage(i.getImage(i.ITEM_HAMMER));
                        }else if(x==i.hammers[1].getPosX() && y==i.hammers[1].getPosY()){
                            mapImageView[index].setImage(i.getImage(i.ITEM_HAMMER));
                        }else{
                            mapImageView[index].setImage(m.mapImage[MapData.TYPE_NONE]);
                        }
                    }
                }
            }
        }
    }

    //リセットボタン
    public void func1ButtonAction(ActionEvent event) { 
        

    }
    public void func2ButtonAction(ActionEvent event) { }
    public void func3ButtonAction(ActionEvent event) { }
    public void func4ButtonAction(ActionEvent event) { }

    public void keyAction(KeyEvent event){
        KeyCode key=event.getCode();
        if (key == KeyCode.UP){
            upButtonAction();
        }else if (key == KeyCode.DOWN){
            downButtonAction();
        }else if (key == KeyCode.LEFT){
            leftButtonAction();
        }else if (key == KeyCode.RIGHT){
            rightButtonAction();
        }
    }

    
    public void downButtonAction(){
        System.out.println("DOWN");
        chara.setCharaDir(MoveChara.TYPE_DOWN);
        chara.move(0, 1);
        mapPrint(chara, mapData,items);
    }
   
    public void downButtonAction(ActionEvent event) {
        downButtonAction();
    }

    public void rightButtonAction(){
        System.out.println("RIGHT");
        chara.setCharaDir(MoveChara.TYPE_RIGHT);
        chara.move( 1, 0);
        mapPrint(chara, mapData,items);
    }

    public void rightButtonAction(ActionEvent event) {
        rightButtonAction();
    }


    public void leftButtonAction(){
        System.out.println("Left");
        chara.setCharaDir(MoveChara.TYPE_LEFT);
        chara.move( -1, 0);
        mapPrint(chara, mapData, items);
    }

    public void leftButtonAction(ActionEvent event) {
        leftButtonAction();
    }

    public void upButtonAction(){
        System.out.println("UP");
        chara.setCharaDir(MoveChara.TYPE_UP);
        chara.move( 0, -1);
        mapPrint(chara, mapData, items);
    }

    public void upButtonAction(ActionEvent event) {
        upButtonAction();
    }
}

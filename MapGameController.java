import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.util.Duration;

public class MapGameController implements Initializable {
    private Image gameOverImage;
    private ImageView[] mapImageView;
    private MapData mapData;
    private MapData initMapData;
    private MapData cloneMapData;
    private ItemController itemController;
    private ItemController initItemController;
    private ItemController cloneItemController;
    private MoveChara chara;
    private MoveChara initChara;
    private MoveChara cloneChara;
    private MoveCaw enemy;
    private MoveCaw initEnemy;
    private MoveCaw cloneEnemy;
    private boolean right_pass; //パスワード正解フラグ
    private boolean input_pass; //パスワード入力フラグ
    private boolean isGameOver; //ゲームオーバー
    private int range; //ライトの有効範囲
    private final int timeLimit = 90; //90s

    @FXML
    private GridPane mapGrid;

    // アイテムポーチのビュー関連
    private int topFocus; //ポーチのイメージビューの右側に表示するポーチのインデックス 
    private ImageView[] poachImageView; //アイテムポーチの所持アイテムのビュー
    private Circle[] clipCircle; //クリッピングのための円
    private Poach selectedItem;// アイテムポーチのビューの真ん中のやつ
    @FXML
    private ImageView poachRightImageView;
    @FXML
    private ImageView poachCenterImageView;
    @FXML
    private ImageView poachLeftImageView;
    @FXML
    private Label selectedItemLabel;

    @FXML
    private Label timeLimitLabel;//制限時間ラベル
    public Timeline timer;

    @FXML
    private Label keyWordLengthLabel;//ヒント(文字数のやつ)ラベル
    @FXML
    private Label obtainedLetterLabel;//入手文字を表示するラベル
    @FXML
    private ImageView gameOverView;

    //キーワード入力画面関連
    private TextField passField;
    private ImageView judgeImageView;
    private Image maruImage;
    private Image batsuImage;
    private Stage passStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gameOverImage = new Image("image/GAMEOVER.png");
        gameOverView.setImage(gameOverImage);
        gameOverView.setOpacity(0);
        isGameOver = false;

        initTimer();

        initGoalStage();

        createNewStage();
    }


    //残り時間タイマーの初期化
    private void initTimer(){
        timeLimitLabel.setText(timeLimit+"");
        timer = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                int restTime = Integer.parseInt(timeLimitLabel.getText());
                if(0 < restTime){
                    restTime--;
                    if(itemController.timerpuls == true){
                        itemController.timerpuls = false;
                        restTime += 20;
                    }
                }else{
                    gameOver();
                }
                timeLimitLabel.setText(restTime+"");
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
    }

	//ゲームオーバー時の処理...
    private void gameOver(){
        timer.stop();
        gameOverView.setOpacity(0.75);
        isGameOver = true;
    }

    //ゴール時のパスワード入力画面初期化
    //fxmlで出来なくてごめんなさい
    public void initGoalStage(){
        Button decideBtn = new Button("決定");
        decideBtn.setDefaultButton(true);
        decideBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e){
                if(passField.getText() != null  && passField.getText()!=""){
                    if(passField.getText().equals(mapData.getKeyWord())){
                        judgeImageView.setImage(maruImage);
                        right_pass = true;
                    }else{
                        judgeImageView.setImage(batsuImage);
                        right_pass = false;
                    }
                }
            }
        });
        passField = new TextField();
        HBox hbTop = new HBox(passField,decideBtn);

        maruImage = new Image("image/MARU.png");
        batsuImage = new Image("image/BATSU.png");
        judgeImageView = new ImageView();
        judgeImageView.setFitWidth(200);
        judgeImageView.setFitHeight(200);
        HBox hbCent = new HBox(judgeImageView);
        hbCent.setPadding(new Insets(10,10,10,10));

        Button closeBtn = new Button("閉じる");
        closeBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e){
                passStage.close();
                input_pass = false;
            }
        });
        HBox hbBotL = new HBox(closeBtn);
        hbBotL.setAlignment(Pos.CENTER_LEFT);
        hbBotL.setPadding(new Insets(0,40,0,0));
        
        Button nextBtn = new Button("進む");
        nextBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e){
                if(right_pass){
                    nextStage();
                }else{
                }
            }
        });
        HBox hbBotR = new HBox(nextBtn);
        hbBotR.setAlignment(Pos.CENTER_RIGHT);
        hbBotR.setPadding(new Insets(0,0,0,40));

        HBox hbBot = new HBox(hbBotL,hbBotR);
        hbBot.setAlignment(Pos.CENTER);

        VBox vb = new VBox(hbTop,hbCent,hbBot);
        vb.setPadding(new Insets(10,10,10,10));

        Pane root = new Pane(vb);
        Scene passScene = new Scene(root);
        passStage = new Stage();
        passStage.setScene(passScene);

        right_pass = false;
        input_pass = false;
    }


    // 敵が動いたときに呼び出される(画面を更新する)
    private UpdateEvent.Listener updateListener =
        new UpdateEvent.Listener() {
            @Override
            public void valueChanged(UpdateEvent.Event e)
            {
                mapPrint(chara, mapData, itemController);
            }
        };

    // マップの初期化
    private void createNewStage(){
        mapData = new MapData(21,15);
        itemController = new ItemController(mapData);
        chara = new MoveChara(1,1,mapData,itemController);
        if(enemy != null) enemy.stop();
        enemy = new MoveCaw(mapData, itemController, chara);
        saveInitData();//マップ生成時の状態を保存
        ((UpdateEvent)enemy).addListener(this.updateListener);
        mapImageView = new ImageView[mapData.getHeight()*mapData.getWidth()];
        for(int y=0; y<mapData.getHeight(); y++){
            for(int x=0; x<mapData.getWidth(); x++){
                int index = y*mapData.getWidth() + x;
                mapImageView[index] =new ImageView();
                mapGrid.add(mapImageView[index], x, y);
            }
        }
        initPoachView();
        keyWordLengthLabel.setText(mapData.getKeyWord().length() + "文字");
        obtainedLetterLabel.setText("");
        range = 3;
        mapPrint(chara, mapData, itemController);

        timer.play();

        enemy.start();
    }

    // オーバーロード
    // マップの初期化
    //   振り出しに戻る・リセット機能用
    private void createNewStage(MapData m,ItemController i,MoveChara cha, MoveCaw caw){
        mapData = m;
        itemController = i;
        chara = cha;
        enemy = caw;
        ((UpdateEvent)enemy).addListener(this.updateListener);
        mapImageView = new ImageView[mapData.getHeight()*mapData.getWidth()];
        for(int y=0; y<mapData.getHeight(); y++){
            for(int x=0; x<mapData.getWidth(); x++){
                int index = y*mapData.getWidth() + x;
                mapImageView[index] =new ImageView();
                mapGrid.add(mapImageView[index], x, y);
            }
        }
        itemController.initPoach();
        initPoachView();
        keyWordLengthLabel.setText(mapData.getKeyWord().length() + "文字");
        obtainedLetterLabel.setText("");
        range = 3;
        mapPrint(chara, mapData, itemController);

        timer.play();

        enemy.start();
    }

    //マップ生成時のオブジェクトのクローンを生成
    private void saveInitData(){
        try{
            initMapData = mapData.clone();
            initItemController = itemController.clone();
            initChara = chara.clone();
            initEnemy = enemy.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
    }

    // initPoachViewメソッド
    // アイテムポーチのビュー関連の初期化
    private void initPoachView(){
        poachImageView = new ImageView[3];
        poachImageView[0] = poachRightImageView;
        poachImageView[1] = poachCenterImageView;
        poachImageView[2] = poachLeftImageView;
        clipCircle = new Circle[3];
        clipCircle[0] = new Circle(poachLeftImageView.getX()+20,poachLeftImageView.getY()+20,20);
        clipCircle[1] = new Circle(poachCenterImageView.getX()+40,poachCenterImageView.getY()+40,40);
        clipCircle[2] = new Circle(poachRightImageView.getX()+20,poachRightImageView.getY()+20,20);
        topFocus = 1;
        scrollPoach();
    }

    //mapPrintメソッド
    //parameter
    //  MoveChara c   : 操作するキャラクター
    //  MapData m     : マップデータ
    //  ItemController i : アイテム 
    //
    //アーキテクチャに従って実際にイメージを貼っていく
    //
    public void mapPrint(MoveChara c, MapData m, ItemController i){
        int theObject;

        if(enemy.getPosX() == c.getPosX() && enemy.getPosY() == c.getPosY()) {
            // こんなところに入れてごめんなさい＞＜
            if(enemy.isstoped()){
                //罠に引っかかっている
            }else{
                gameOver();
            }
        }

        for(int y=0; y<mapData.getHeight(); y++){
            for(int x=0; x<mapData.getWidth(); x++){
                int index = y*mapData.getWidth() + x;
                if (x==c.getPosX() && y==c.getPosY()){
                    if(c.getPosX() != enemy.getPosX() || c.getPosY() != enemy.getPosY()){
                        mapImageView[index].setImage(c.getImage());
                    }
                }else if(!canReachLight(x,y)){
                    mapImageView[index].setImage(m.getImage(MapData.TYPE_DARK));
                }else if(x==enemy.getPosX()&& y==enemy.getPosY()) {
                    mapImageView[index].setImage(enemy.getImage());
                }else{
                    theObject = mapData.getMap(x,y);
                    if(theObject == MapData.TYPE_ITEM){
                        mapImageView[index].setImage(i.getItemImage(x,y));
                    }else if(theObject == MapData.TYPE_TRAP){
                        mapImageView[index].setImage(i.getTrapImage(x,y));
                    }else{
                        mapImageView[index].setImage(m.getImage(x,y));
                    }
                }
            }
        }
    }

    //mapPrintメソッド(オーバーロード)
    //parameter
    //  MoveChara c   : 操作するキャラクター
    //  MapData m     : マップデータ
    //
    //キャラクターとその正面の位置だけにイメージをセットする
    //
    public void mapPrint(MoveChara c){
        int[] charaDirPos;
        int index;
        int frontPosX,frontPosY;
        int theObject;

        //charaのイメージをセット
        index = c.getPosY()*mapData.getWidth() + c.getPosX();
        mapImageView[index].setImage(c.getImage());

        //charaの正面のイメージをセット
        charaDirPos = c.getCharaDirPos();
        frontPosX = charaDirPos[0];
        frontPosY = charaDirPos[1];
        theObject = mapData.getMap(frontPosX,frontPosY);
        index = frontPosY*mapData.getWidth() + frontPosX;
        if(theObject == MapData.TYPE_TRAP){
            mapImageView[index].setImage(itemController.getTrapImage(frontPosX,frontPosY));
        }else if(theObject == MapData.TYPE_ITEM){
            mapImageView[index].setImage(itemController.getItemImage(frontPosX,frontPosY));
        }else{
            mapImageView[index].setImage(mapData.getImage(frontPosX,frontPosY));
        }
    }

    //mapPrintメソッド(オーバーロード)
    //parameter
    //  MapData m     : マップデータ
    //
    //壁だけのイメージをセットする
    //
    public void mapPrint(MapData m){
        for(int y=0; y<m.getHeight(); y++){
            for(int x=0; x<m.getWidth(); x++){
                int index = y*m.getWidth() + x;
                if(canReachLight(x,y)){
                    if(m.getMap(x,y) == MapData.TYPE_WALL){
                        mapImageView[index].setImage(m.getImage(x,y));
                    }
                }
            }
        }
    }

    //canReachLightメソッド
    //  parameter 
    //    int x,y : 座標
    //  return 
    //    指定された座標がライトが届く範囲(visibility)だったらtrueを返す
    //
    //  (x,y)がライトの光が届く範囲かどうかを確認する
    //
    private boolean canReachLight(int x, int y){
        if(Math.abs(x-chara.getPosX()) > range || Math.abs(y-chara.getPosY()) > range){
            return false;
        }else{
            return true;
        }
    }

	//背景チェンジ
    public void func1ButtonAction(ActionEvent event) {
        mapData.func1();
        mapPrint(mapData);
    }
    public void func2ButtonAction(ActionEvent event) {
        mapData.func2();
        mapPrint(mapData);
    }
    public void func3ButtonAction(ActionEvent event) {
        mapData.func3();
        mapPrint(mapData);
    }
    public void func4ButtonAction(ActionEvent event) {
        mapData.func4();
        mapPrint(mapData);
        //mapData.printMap();//テスト
    }
    
	//リスタート(現状牛の動きが不安定)
	//マップ生成時のクローンをもとにクローンを複製
    public void restartButtonAction(ActionEvent event){
        try{
            cloneMapData = initMapData.clone();

            cloneItemController = initItemController.clone();
            cloneItemController.setInitObj(cloneMapData);

            cloneChara = initChara.clone();
            cloneChara.setInitObj(cloneMapData,cloneItemController);

            enemy.stop(); /* たぶん止めないといけないです */
            cloneEnemy = initEnemy.clone();
            cloneEnemy.setInitObj(cloneMapData, cloneItemController, cloneChara);

        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        createNewStage(cloneMapData, cloneItemController,cloneChara,cloneEnemy);
        timeLimitLabel.setText(timeLimit+"");
        gameOverView.setOpacity(0);
        isGameOver = false;
    }

    public void keyAction(KeyEvent event){
        KeyCode key = event.getCode();
        if(input_pass || isGameOver){
            //パスワード入力時/ゲームオーバー時はキー入力無効
            return;
        }else{
            if(key == KeyCode.DOWN || key == KeyCode.UP){
                charaMove(key);
            }else if(key == KeyCode.LEFT || key == KeyCode.RIGHT){
                if(event.isControlDown()){
                    if(itemController.poachHasItem()){
                        if(canScrollPoach(key)){
                            scrollPoach(key);
                        }else{
                            System.out.println("you can not scroll poach.");
                        }
                    }else{
                        System.out.println("You have no Item.");
                    }
                }else{
                    charaMove(key);
                }
            }else if (key == KeyCode.H || key == KeyCode.U){
                charaAction(key);
            }
        }
    }

    // charaMoveメソッド
    //   parameter
    //     keyCode key : 矢印キーのみ
    // キャラクターのキーに応じた移動
    //
    public void charaMove(KeyCode key)
    {
        if(key == KeyCode.DOWN) {
            downButtonAction();
        }else if (key == KeyCode.RIGHT){
            rightButtonAction();
        }else if(key == KeyCode.UP) {
            upButtonAction();
        } else if(key == KeyCode.LEFT) {
            leftButtonAction();
        }

        if(reachGoal()){
            passStage.show();
            input_pass = true;//パスワード入力中
        }
    }

    // charaActionメソッド
    //   parameter
    //     keyCode key : 移動キー以外のキー 
    // キャラクターのキーに応じた動き
    //
    public void charaAction(KeyCode key){
        if (key == KeyCode.H){
            if(chara.destroyStone(chara.PUNCH)){
                System.out.println("detroy!"); 
                mapPrint(chara);
            }else{
                System.out.println("punch!");//猫パンチ
            }
        }else if(key == KeyCode.U){
            // アイテムを使用 
            chara.useItem(selectedItem);
            mapPrint(chara);
            scrollPoach();
        } 
    }

    // scrollPoachメソッド
    //   parameter --
    //   return    --
    // アイテムポーチのビューの初期イメージ/拾ったアイテムのイメージをセット
    //
    private void scrollPoach(){
        Poach poachData;
        for(int i = 0;i < 3;i++){
            poachData = itemController.getPoachData(topFocus+i);
            poachImageView[i].setImage(itemController.getItemImage(poachData.getItemType()));
            poachImageView[i].setClip(clipCircle[i]);
            if(i == 1){
                selectedItemLabel.setText(poachData.toString());
                selectedItem = poachData;
            }
        }
    }

    // scrollPoach(オーバーロード)
    //   parameter
    //     KeyCode key : CTR + 方向キーの左か右のキー
    //   return --
    // CTR-←  CTR-→  のときのアイテムポーチのビューのイメージをセット
    //
    private void scrollPoach(KeyCode key){
        Poach poachData;
        if(key == KeyCode.LEFT){
            topFocus++;
            for(int i = 0;i < 3;i++){
                poachData = itemController.getPoachData(topFocus+i);
                poachImageView[i].setImage(itemController.getItemImage(poachData.getItemType()));
                poachImageView[i].setClip(clipCircle[i]);
                if(i == 1){
                    selectedItemLabel.setText(poachData.toString());
                    selectedItem = poachData;
                }
            }
        }else{
            topFocus--;
            for(int i = 0;i < 3;i++){
                poachData = itemController.getPoachData(topFocus+i);
                poachImageView[i].setImage(itemController.getItemImage(poachData.getItemType()));
                poachImageView[i].setClip(clipCircle[i]);
                if(i == 1){
                    selectedItemLabel.setText(poachData.toString());
                    selectedItem = poachData;
                }
            }
        }
    }

    //canScrollPoachメソッド
    //  parameter
    //    KeyCode key : アイテムポーチをスクロールする方向
    //  return 
    //    boolean     : スクロールできればtrueを返す
    //
    private boolean canScrollPoach(KeyCode key){
        if(key == KeyCode.LEFT){
            if(topFocus < itemController.getPossessionNum()+1){
                return true;
            }else{
                return false;
            }
        }else{
            if(0 < topFocus){
                return true;
            }else{
                return false;
            }
        }
    }

    //reachGoalメソッド
    //prameter 
    //  --
    //return 
    //  boolean : ゴールポイントならtrueを返す
    //  
    public boolean reachGoal(){
        if(chara.getPosX() == MapData.GOAL_X && chara.getPosY() == MapData.GOAL_Y){
            return true;
        }else{
            return false;
        }
    }

    //nextStageメソッド
    //  次のステージに行く
    private void nextStage(){
        passStage.close();
        createNewStage();
        timeLimitLabel.setText(timeLimit+"");
        passField.setText("");
        judgeImageView.setImage(null);
        right_pass = false;
        input_pass = false;
    }

    public void downButtonAction(){
        int theObject;
        chara.setCharaDir(MoveChara.TYPE_DOWN);
        chara.move(0, 1);
        theObject = mapData.getMap(chara.getPosX(),chara.getPosY());
        if(theObject == MapData.TYPE_ITEM){
            scrollPoach();
            mapData.setMap(chara.getPosX(),chara.getPosY(),MapData.TYPE_NONE);
        }else if(theObject == MapData.TYPE_KEYWORD){
            obtainedLetterLabel.setText(itemController.getKeyLetterPoach());
            mapData.setMap(chara.getPosX(),chara.getPosY(),MapData.TYPE_NONE);
        }
        mapPrint(chara, mapData ,itemController);
    }

    public void downButtonAction(ActionEvent event) {
        downButtonAction();
    }

    public void rightButtonAction(){
        int theObject;
        chara.setCharaDir(MoveChara.TYPE_RIGHT);
        chara.move( 1, 0);
        theObject = mapData.getMap(chara.getPosX(),chara.getPosY());
        if(theObject == MapData.TYPE_ITEM){
            scrollPoach();
            mapData.setMap(chara.getPosX(),chara.getPosY(),MapData.TYPE_NONE);
        }else if(theObject == MapData.TYPE_KEYWORD){
            obtainedLetterLabel.setText(itemController.getKeyLetterPoach());
            mapData.setMap(chara.getPosX(),chara.getPosY(),MapData.TYPE_NONE);
        }
        mapPrint(chara, mapData ,itemController);
    }

    public void rightButtonAction(ActionEvent event) {
        rightButtonAction();
    }


    public void leftButtonAction(){
        int theObject;
        chara.setCharaDir(MoveChara.TYPE_LEFT);
        chara.move( -1, 0);
        theObject = mapData.getMap(chara.getPosX(),chara.getPosY());
        if(theObject == MapData.TYPE_ITEM){
            scrollPoach();
            mapData.setMap(chara.getPosX(),chara.getPosY(),MapData.TYPE_NONE);
        }else if(theObject == MapData.TYPE_KEYWORD){
            obtainedLetterLabel.setText(itemController.getKeyLetterPoach());
            mapData.setMap(chara.getPosX(),chara.getPosY(),MapData.TYPE_NONE);
        }
        mapPrint(chara, mapData ,itemController);
    }

    public void leftButtonAction(ActionEvent event) {
        leftButtonAction();
    }

    public void upButtonAction(){
        int theObject;
        chara.setCharaDir(MoveChara.TYPE_UP);
        chara.move( 0, -1);
        theObject = mapData.getMap(chara.getPosX(),chara.getPosY());
        if(theObject == MapData.TYPE_ITEM){
            scrollPoach();
            mapData.setMap(chara.getPosX(),chara.getPosY(),MapData.TYPE_NONE);
        }else if(theObject == MapData.TYPE_KEYWORD){
            obtainedLetterLabel.setText(itemController.getKeyLetterPoach());
            mapData.setMap(chara.getPosX(),chara.getPosY(),MapData.TYPE_NONE);
        }
        mapPrint(chara, mapData ,itemController);
    }
}

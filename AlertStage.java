//Here is a method that will display the confirmation alert (stage) when a user wishes to delete an item.
    public void displayAlert(ObservableList<Columns> itemsToDelete){//Columns rowinfo) {

        Stage alert = new Stage();
        opaqueLayer.setVisible(true);

        String striphex = "#610031;";
        String alertmidhex = "#dfccd5;";
        String stripcolour = String.format("-fx-background-color: %s", striphex);
        String alertmidcolour = String.format("-fx-background-color: %s", alertmidhex);

        double alertwidth = 500.0;
        double alertw_to_h = 1.42857;
        double alertheight = alertwidth / alertw_to_h;

        Bounds sb = base.localToScreen(base.getBoundsInLocal());

        screenX = (sb.getMinX() + w / 2 - alertwidth / 2); 
        screenY = (sb.getMinY() + h / 2 - alertheight / 2);

        alert.initStyle(StageStyle.UNDECORATED);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(Kettlelog.setup);

        //Ensures that alert popup is centered relatively to its parent stage (setup).
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            alert.setX(screenX);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            alert.setY(screenY);
        };

        alert.widthProperty().addListener(widthListener);
        alert.heightProperty().addListener(heightListener);

        alert.setOnShown(shown -> {
            alert.widthProperty().removeListener(widthListener);
            alert.heightProperty().removeListener(heightListener);
        });

        //Top part of the pane which says "Confirm Deletion.""
        Text deltext = new Text();
            deltext.setText("Confirm Deletion");
            deltext.setFont(new Font(18));
            deltext.setFill(Color.WHITE);

        AnchorPane alerttstrip = new AnchorPane();
            AnchorPane.setLeftAnchor(deltext, 30.0);
            AnchorPane.setBottomAnchor(deltext, 10.0);
            alerttstrip.setStyle(stripcolour);
            alerttstrip.setPrefSize(alertwidth, 75); //(width, height)
            alerttstrip.getChildren().addAll(deltext);

        //Center part of the pane which contains the Kettlelog logo and some text labels.

        Text delconfirm = new Text();
            delconfirm.setText("Are you sure you want to delete");
            delconfirm.setFont(new Font(16));

        Image kettleonlyimage = new Image("./Misc/kettle.png");
        ImageView kettle = new ImageView();
            kettle.setFitHeight(150);
            kettle.setFitWidth(150);
            kettle.setImage(kettleonlyimage);

        Label itemlabel = new Label();
            if(itemsToDelete.size()==1){
                itemlabel.setText(itemsToDelete.get(0).getName() + "?");
            }
            else{
                itemlabel.setText("the selected items?");
            }
            itemlabel.setFont(new Font(16));
            itemlabel.setPrefHeight(50.0);
            itemlabel.setPrefWidth(280.0);
            itemlabel.setAlignment(Pos.CENTER);
            itemlabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            itemlabel.setStyle("-fx-background-color: #cbadc3;");

        Text delperm = new Text();
            if(itemsToDelete.size()==1){
                delperm.setText("This item will be deleted permanently.");
            }
            else{
                delperm.setText("The items will be deleted permanently.");
            }
            delperm.setFont(new Font(14));

        Text delundo = new Text();
            delundo.setText("You can't undo this action.");
            delundo.setFont(new Font(14));

        VBox alertcentervbox = new VBox(10);
            alertcentervbox.setPrefSize(220.0, 230.0);
            alertcentervbox.getChildren().addAll(delconfirm, itemlabel, delperm, delundo);
            alertcentervbox.setPadding(new Insets(50.0, 0.0, 0.0, 0.0));
            //alertcentervbox.setStyle("-fx-background-color: #cf1020;");
 
        AnchorPane alertcenter = new AnchorPane();
            AnchorPane.setTopAnchor(alertcentervbox, 0.0);
            AnchorPane.setRightAnchor(alertcentervbox, 30.0);
            AnchorPane.setLeftAnchor(kettle, 10.0);
            AnchorPane.setTopAnchor(kettle, 40.0);

            alertcenter.setStyle(alertmidcolour);
            alertcenter.getChildren().addAll(kettle, alertcentervbox);

        //Bottom part of the pane which has the two buttons "Cancel" and "Delete".  
        Button alertcancel = new Button();
            alertcancel.setText("Cancel");
            alertcancel.setPrefHeight(30);

            alertcancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                    public void handle(ActionEvent event) {
                        searchbar.clear();
                        alert.hide();
                        opaqueLayer.setVisible(false);
                        itemsToDelete.clear();
                    }
                }); 

        Button alertdelete = new Button();
            alertdelete.setText("Delete Item");
            alertdelete.setPrefHeight(30);

            alertdelete.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                    public void handle(ActionEvent event) {
                        //System.out.println("Old size: "+ data.size());
                        for(int i = 0; i<itemsToDelete.size(); i++){ //cycle thru itemsToDelete list and remove from data 
                            data.remove(itemsToDelete.get(i));
                            //System.out.println("Deleted: " +itemsToDelete.get(i).getName());
                        }
                        itemsToDelete.clear(); //clear itemsToDelete list
                        removeBtn.setDisable(true);

                        //System.out.println("New size: "+ data.size());
                        for(int j = 0; j<data.size(); j++){//search items for checked property
                            if((data.get(j)).getChecked()==true){
                                removeBtn.setDisable(false);
                            }
                        }
                        searchbar.clear();
                        if(data.size()==0){
                            data.add(empty);
                        }
                        CellGenerator cellFactory = new CellGenerator();    
                        columns[0].setCellFactory(cellFactory);
                        table.setItems(data);

                        alert.hide();
                        opaqueLayer.setVisible(false);
                    }
                }); 

        HBox alertbbx = new HBox(15);
            alertbbx.getChildren().addAll(alertcancel, alertdelete);

        AnchorPane alertbstrip = new AnchorPane();
            AnchorPane.setRightAnchor(alertbbx, 7.5);
            AnchorPane.setTopAnchor(alertbbx, 7.5);
            alertbstrip.setStyle(stripcolour);
            alertbstrip.setPrefSize(alertwidth, 50); //(width, height)
            alertbstrip.getChildren().addAll(alertbbx);
    
        BorderPane alertpane = new BorderPane();

        alertpane.setTop(alerttstrip);
        alertpane.setCenter(alertcenter);
        alertpane.setBottom(alertbstrip);

        alert.setResizable(false);
        alert.setScene(new Scene(alertpane, alertwidth, alertheight));
        alert.showAndWait();
    }
//Here is a singular method that allows a user to either Edit or Add an item depending on the parameters specified.
    public void addItemPopup(int popuptype, String[]textarray, Columns rowinfo){
        //0 --> ADD WINDOW
        //1 --> EDIT WINDOW

        //PRE-SET TEXT FIELDS TAKEN FROM 5-ELEMENT ARRAY
        //This array takes the name, quantity, minimum, shipping time, description and date of the column.
        //The purpose of this is to save the information so that it can be displayed when the edit button is clicked.
        String prename = textarray[0];
        String prequan = textarray[1];
        String premin = textarray[2];
        String predel = textarray[3];
        String predesc = textarray[4];
        String predate = textarray[5];
        
        //COLOURS
        Stage addwindow = new Stage();
        String tbcolour = "#006733;";
        String midcolour = "#d5f0e2;";
        String topbottom = String.format("-fx-background-color: %s", tbcolour);
        String middle = String.format("-fx-background-color: %s", midcolour);

        Bounds sb = base.localToScreen(base.getBoundsInLocal());

        //Variable initialization
        AnchorPane addbottom = new AnchorPane();
        HBox bottomBox = new HBox(10);
        Button cancelbtn = new Button();
        Button addbtn = new Button();
        bottomBox.getChildren().addAll(cancelbtn, addbtn);

        opaqueLayer.setVisible(true);
        double addwidth = 600;
        double addw_to_h = 0.85;
        double addheight = addwidth / addw_to_h;
        screenX = (sb.getMinX() + w / 2 - addwidth / 2); 
        screenY = (sb.getMinY() + h / 2 - addheight / 2);

        //TOP PART of the window which includes a title and a logo.
        AnchorPane addtop = new AnchorPane();
        addtop.setStyle(topbottom);
        addtop.setPrefSize(60, 80); 

        //ADD TITLE
        Text addtext = new Text();
        if (popuptype == 0) {
            addtext.setText("Add New Item");
        } else {
            addtext.setText("Edit Item");
        }

        addtext.setFont(new Font(18));
        addtext.setFill(Color.WHITE);
        AnchorPane.setLeftAnchor(addtext, 42.0);
        AnchorPane.setBottomAnchor(addtext, 10.0);
        addtop.getChildren().addAll(addtext);

        //BOTTOM PART of the window which includes an "Add" and "Cancel" button.
        addbottom.setStyle(topbottom);
        addbottom.setPrefSize(30, 40); 

        if (popuptype == 0) {
            addbtn.setText("Create");
        } else {
            addbtn.setText("Edit");
        }
        
        addbtn.setId("createditem");  
        addbtn.setStyle("-fx-background-color: #093d23;");
        addbtn.setTextFill(Color.WHITE);
        cancelbtn.setText("Cancel");
        cancelbtn.setId("cancelBtn");
        cancelbtn.setStyle("-fx-background-color: #d5f0e2;");
        AnchorPane.setRightAnchor(bottomBox, 6.25);
        AnchorPane.setTopAnchor(bottomBox, 6.25);
        addbottom.getChildren().addAll(bottomBox);

        //BASE BORDER (CENTER WILL BE THE TEXT FIELDS)
        BorderPane abase = new BorderPane();
        VBox wcenter = new VBox();
        abase.setTop(addtop);
        abase.setBottom(addbottom);


        //================================================================================
        // WINDOW CONTENTS (LABELS & TEXT BOXES)
        //================================================================================

        //VARIABLE INTIALIZATION
        double numbertextwidth = 100.0;

        //ITEM NAME ~ REQUIRED FIELD
        AnchorPane ianchor = new AnchorPane();
        ianchor.setPrefSize(addwidth, 80);

        Text itemname = new Text("Item Name:");
        Font f = new Font(15);
            itemname.setFont(f);
            itemname.setFill(Color.BLACK);
        AnchorPane.setRightAnchor(itemname, 460.0);
        AnchorPane.setBottomAnchor(itemname, 15.0);

        Text a = new Text("*");
            a.setFont(new Font (15));
            a.setFill(Color.RED);
        AnchorPane.setRightAnchor(a, 545.0);
        AnchorPane.setBottomAnchor(a, 16.0);

        TextField itemtext = new TextField();
            itemtext.setPrefWidth(150);
            itemtext.setText(prename);
        AnchorPane.setLeftAnchor(itemtext, 150.0); 
        AnchorPane.setBottomAnchor(itemtext, 10.0);

        //Icon taken from flaticon.com
        Image helpBtnImg = new Image("./Misc/help.png");
        ImageView helpImg = new ImageView(); 
            helpImg.setImage(helpBtnImg);
            helpImg.setFitWidth(20);
            helpImg.setPreserveRatio(true);
            helpImg.setSmooth(true);
            helpImg.setCache(true);  

        //instruction tooltip for help button
        String addtip = "add";
        String edittip = "edit";
        Tooltip helptip = new Tooltip();
        //helptip.setShowDuration(javafx.util.Duration.seconds(60));
        if (popuptype == 0) {
            helptip.setText(addtip);
        } else {
            helptip.setText(edittip);
        }

        Button helpBtn = new Button();
            helpBtn.setGraphic(helpImg);    
            helpBtn.setStyle("-fx-background-color: transparent;");    
            helpBtn.setTooltip(helptip);     
            AnchorPane.setRightAnchor(helpBtn, 175.0);
            AnchorPane.setBottomAnchor(helpBtn, 8.0);
            
        //datepicker where user can select when the action occurred
        DatePicker datepicker = new DatePicker();
            datepicker.setPrefWidth(125);
            datepicker.setEditable(false);
            if (popuptype == 0) {
                datepicker.setValue(LocalDate.now());
            } else {
                LocalDate originaldate = LocalDate.parse(predate);
                datepicker.setValue(originaldate);
            }
            
        AnchorPane.setRightAnchor(datepicker, 50.0);
        AnchorPane.setBottomAnchor(datepicker, 10.0);

        ianchor.setStyle(middle);
        ianchor.getChildren().addAll(itemname, itemtext, a, datepicker, helpBtn);
        HBox iBox = new HBox(ianchor);

        //QUANTITY ~ REQUIRED FIELD
        AnchorPane qanchor = new AnchorPane();
        qanchor.setPrefSize(addwidth, 90);

        Text quantity = new Text("Quantity:");
            quantity.setFont(f);
            quantity.setFill(Color.BLACK);
        AnchorPane.setRightAnchor(quantity, 460.0);
        AnchorPane.setBottomAnchor(quantity, 45.0);

        Text a2 = new Text("*");
            a2.setFont(new Font (15));
            a2.setFill(Color.RED);
        AnchorPane.setRightAnchor(a2, 527.8);
        AnchorPane.setBottomAnchor(a2, 46.0);

        TextField qtext = new TextField();
            qtext.setPrefWidth(numbertextwidth);
            qtext.setText(prequan);
        AnchorPane.setLeftAnchor(qtext, 150.0); 
        AnchorPane.setBottomAnchor(qtext, 40.0);

        // QUANTITY TEXT FIELD MUST BE NUMERIC
        qtext.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    qtext.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        Text qdesc = new Text("How much of this item do you currently have on hand?");
            qdesc.setFont(new Font(12));
            qdesc.setFill(Color.GREY);
        AnchorPane.setLeftAnchor(qdesc, 150.0);
        AnchorPane.setBottomAnchor(qdesc, 20.0);

        Text qdesc2 = new Text("Entry should be in singular units (eg. 3 boxes of 50 gloves = 150).");
            qdesc2.setFont(new Font(12));
            qdesc2.setFill(Color.GREY);
        AnchorPane.setLeftAnchor(qdesc2, 150.0);
        AnchorPane.setBottomAnchor(qdesc2, 5.0);
 
        qanchor.setStyle(middle);
        qanchor.getChildren().addAll(quantity, qtext, qdesc, qdesc2, a2);
        HBox qBox = new HBox(qanchor);

        //MINIMUM ~ REQUIRED FIELD
        AnchorPane manchor = new AnchorPane();
        manchor.setPrefSize(addwidth, 90);

        Text minimum = new Text("Minimum:");
            minimum.setFont(f);
            minimum.setFill(Color.BLACK);
        AnchorPane.setRightAnchor(minimum, 460.0);
        AnchorPane.setBottomAnchor(minimum, 45.0);

        Text a3 = new Text("*");
            a3.setFont(new Font (15));
            a3.setFill(Color.RED);
        AnchorPane.setRightAnchor(a3, 532.0);
        AnchorPane.setBottomAnchor(a3, 46.0);

        TextField mtext = new TextField();
            mtext.setPrefWidth(numbertextwidth);
            mtext.setText(premin);
        AnchorPane.setLeftAnchor(mtext, 150.0); 
        AnchorPane.setBottomAnchor(mtext, 40.0);

        // MINIMUM TEXT FIELD MUST BE NUMERIC
        mtext.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    mtext.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        Text mdesc = new Text("What is the minimum number of this item you want in your office?");
            mdesc.setFont(new Font(12));
            mdesc.setFill(Color.GREY);
        AnchorPane.setLeftAnchor(mdesc, 150.0);
        AnchorPane.setBottomAnchor(mdesc, 20.0);

        Text mdesc2 = new Text("Entry should be in singular units (eg. 3 boxes of 50 gloves = 150).");
            mdesc2.setFont(new Font(12));
            mdesc2.setFill(Color.GREY);
        AnchorPane.setLeftAnchor(mdesc2, 150.0);
        AnchorPane.setBottomAnchor(mdesc2, 5.0);
 
        manchor.setStyle(middle);
        manchor.getChildren().addAll(minimum, mtext, mdesc, mdesc2, a3);
        HBox mBox = new HBox(manchor);

        //SHIPPING ~ REQUIRED FIELD
        AnchorPane sanchor = new AnchorPane();
        sanchor.setPrefSize(addwidth, 90);

        Text shipping = new Text("Delivery Time:");
            shipping.setFont(f);
            shipping.setFill(Color.BLACK);
        AnchorPane.setRightAnchor(shipping, 460.0);
        AnchorPane.setBottomAnchor(shipping, 45.0);

        Text a4 = new Text("*");
            a4.setFont(new Font (15));
            a4.setFill(Color.RED);
        AnchorPane.setRightAnchor(a4, 565.0);
        AnchorPane.setBottomAnchor(a4, 46.0);

        TextField stext = new TextField();
            stext.setText(predel);
            stext.setPrefWidth(numbertextwidth);
        AnchorPane.setLeftAnchor(stext, 150.0); 
        AnchorPane.setBottomAnchor(stext, 40.0);

        // DELIVERY TEXT FIELD MUST BE NUMERIC
        stext.textProperty().addListener(new ChangeListener<String>() {
        @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    stext.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        Text sdesc = new Text("An estimate of how long the item would take to deliver to your location.");
            sdesc.setFont(new Font(12));
            sdesc.setFill(Color.GREY);
        AnchorPane.setLeftAnchor(sdesc, 150.0);
        AnchorPane.setBottomAnchor(sdesc, 20.0);

        Text sdesc2 = new Text("Entry should be in the number of days (if bought in person, enter 0).");
            sdesc2.setFont(new Font(12));
            sdesc2.setFill(Color.GREY);
        AnchorPane.setLeftAnchor(sdesc2, 150.0);
        AnchorPane.setBottomAnchor(sdesc2, 5.0);
 
        sanchor.setStyle(middle);
        sanchor.getChildren().addAll(shipping, stext, sdesc, sdesc2, a4);
        HBox sBox = new HBox(sanchor);

        //DESCRIPTION BIG H-BOX
        AnchorPane danchor = new AnchorPane();
        danchor.setPrefSize(addwidth, 240);

        Text describe = new Text("Description:");
            describe.setFont(f);
            describe.setFill(Color.BLACK);
        AnchorPane.setRightAnchor(describe, 460.0);
        AnchorPane.setTopAnchor(describe, 20.0);

        TextArea dtext = new TextArea();
            dtext.positionCaret(4);
            dtext.setPrefWidth(400);
            dtext.setPrefHeight(175);
            dtext.setWrapText(true);
            dtext.setText(predesc);

        AnchorPane.setLeftAnchor(dtext, 150.0); 
        AnchorPane.setTopAnchor(dtext, 20.0);

        Text missing = new Text();
            missing.setFont(new Font(12));
            missing.setFill(Color.FIREBRICK);
        AnchorPane.setRightAnchor(missing, 50.0);
        AnchorPane.setBottomAnchor(missing, 15.0);
        missing.setVisible(false);

        danchor.setStyle(middle);
        danchor.getChildren().addAll(describe, dtext, missing);
        HBox dBox = new HBox(danchor);

        //ADDING THE HBOXES TO A VBOX
        wcenter.getChildren().addAll(iBox, qBox, mBox, sBox, dBox);
        abase.setCenter(wcenter);
        addwindow.setScene(new Scene(abase, addwidth, addheight));

        //Ensures that addwindow is centered relatively to its parent stage (setup).
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            addwindow.setX(screenX);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            addwindow.setY(screenY);
        };

        addwindow.widthProperty().addListener(widthListener);
        addwindow.heightProperty().addListener(heightListener);

        addwindow.setOnShown(shown -> {
            addwindow.widthProperty().removeListener(widthListener);
            addwindow.heightProperty().removeListener(heightListener);
        });

        //BOTTOM BUTTONS FUNCTIONALITY
        cancelbtn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
            public void handle(ActionEvent event) {
                addwindow.hide();
                opaqueLayer.setVisible(false);
                duplicatefound = false;
                presscount = 0;
            }
        }); 

        addbtn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
            public void handle(ActionEvent event) {

                if(duplicatefound){
                    presscount++;
                }

                boolean incomplete = false;
                String itemStatus = "";
                String iName = itemtext.getText();
                String curQuan = qtext.getText();
                String minQuan = mtext.getText();
                String delTime = stext.getText();
                String itemDesc = dtext.getText();

                //we need to get the value that the user sets as the date and convert it to a string
                String newdate = datepicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                //CHECKS IF THERE ARE ANY REQUIRED FIELDS THAT ARE LEFT EMPTY
                if ((iName.trim().length() <= 0) || curQuan.isEmpty() || minQuan.isEmpty() || delTime.isEmpty()) {
                    incomplete = true;
                }

                if (incomplete) {
                    missing.setText("* One or more required fields have not been filled out.");
                    missing.setVisible(true);
                }
                else {
                    duplicatefound = false;
                    for (int i = 0; i < data.size(); i++) {
                        if ((data.get(i)).getName().equals(iName)) {
                            duplicatefound = true;  
                        } 
                    }

                    if (!duplicatefound) {
                        presscount = 2;
                    }

                    //EVERY ITEM GETS ASSIGNED A UNIQUE ID WHICH IS THE TIMESTAMP AT WHICH IT WAS CREATED
                    String id = new java.text.SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
                    btnids.add(id);     

                    CellGenerator cellFactory = new CellGenerator();    
                    columns[0].setCellFactory(cellFactory);     

                    missing.setVisible(false);
                    int intQuan = Integer.parseInt(curQuan);
                    int intMin = Integer.parseInt(minQuan);
                    int total = intQuan + intMin;
                    double health = (((double) intQuan / total)) * 100;

                    //DETERMINING THE STATUS OF THE ITEM 
                    if (intQuan == 0) {
                        itemStatus = "Empty";
                    } else if (health < 25) {
                        itemStatus = "Very Poor";
                    } else if (health < 40) {
                        itemStatus = "Poor";
                    } else if (health < 50) {
                        itemStatus = "Moderate";
                    } else if (health < 75) {
                        itemStatus = "Good";
                    } else {
                        itemStatus = "Very Good";
                    }

                    //user tries to add a duplicate for the first time, so we should display the message.
                    if (popuptype == 0 && presscount == 1) {
                        missing.setText("* Possible duplicate found. Are you sure you want to add this item?");
                        missing.setVisible(true);
                    } 

                    //if the type is to add, then add the row.
                    else if (popuptype == 0 && presscount == 2) {
                        String olddate = newdate;
                        data.add(new Columns(iName, itemStatus, curQuan, minQuan, delTime, itemDesc, id, false, false, newdate, olddate));
                        presscount = 0;
                        duplicatefound = false;
                        data.remove(empty);
                        itemsToDelete.remove(empty);

                        searchbar.clear();
                        opaqueLayer.setVisible(false);
                        addwindow.hide();
                        table.setItems(data);
                    }

                    //if the type is to edit, update the information at every field.
                    else {
                        rowinfo.setName(iName);
                        rowinfo.setQuantity(curQuan);
                        rowinfo.setMinimum(minQuan);
                        rowinfo.setDelivery(delTime);
                        rowinfo.setDesc(itemDesc);
                        rowinfo.setDate(newdate);

                        searchbar.clear();
                        opaqueLayer.setVisible(false);
                        addwindow.hide();
                        table.setItems(data);
                    }
                }

                FilterComparators filterObject = new FilterComparators(data, table);
                if(filterSel==1){
                    filterObject.sortByStarred();
                }
                else if(filterSel==2 || filterSel==3){
                    filterObject.sortByMostRecent(filterSel);
                }
            }
        }); 

        //STAGE INFORMATION 
        addwindow.initStyle(StageStyle.UNDECORATED);
        addwindow.initModality(Modality.WINDOW_MODAL);
        addwindow.initOwner(Kettlelog.setup);
        addwindow.setResizable(false);
        addwindow.setTitle("Add New Item");
        addwindow.show();

    }
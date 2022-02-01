package com.lukeonuke.lmark.util;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class StyleRegister {
    //To reduce memory usage we use an indexed system
    ArrayList<Collection<String>> index = new ArrayList<>();
    ArrayList<Integer> styleList;
    final Logger logger = LoggerFactory.getLogger(StyleRegister.class);

    private StyleRegister() {
    }

    public StyleRegister(int size) {
        addToIndex(List.of("fillerx"));
        styleList = new ArrayList<>(Arrays.asList(new Integer[size]));
        Collections.fill(styleList, 0);
    }

    private void addToIndex(Collection<String> styleCollection){
        if(!index.contains(styleCollection)){
            index.add(styleCollection);
        }
    }

    private Collection<String> getFromIndex(int indx){
        return index.get(indx);
    }

    private int lookupIndex(Collection<String> styleCollection){
        return index.indexOf(styleCollection);
    }

    public void setStyleAt(int charAt, Collection<String> styleCollection){
        if(styleList.get(charAt) == 0){
            addToIndex(styleCollection);
            styleList.ensureCapacity(charAt);
            styleList.set(charAt, lookupIndex(styleCollection));
        }else{
            Collection<String> styles = getStyleAt(charAt);
            styleCollection.forEach(s -> {
                if(!styles.contains(s)){
                    styles.add(s);
                }
            });
            addToIndex(styles);
            styleList.set(charAt, lookupIndex(styles));
        }
    }

    public void setStyleBetween(int min, int max, Collection<String> styleCollection){
        if(max > styleList.size() - 1) return;
        for (int i = min; i <= max; i++) {
            setStyleAt(i, styleCollection);
        }
    }

    public Collection<String> getStyleAt(int charAt){
        return getFromIndex(styleList.get(charAt));
    }



    public StyleSpans<Collection<String>> getStyleSpans(){
        int indicator = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        for (int i = 0; i < styleList.size(); i++) {
            if(!styleList.get(indicator).equals(styleList.get(i))){
                spansBuilder.add(getFromIndex(styleList.get(indicator)), i - indicator);
                logger.info("{} from {} to {} length {}", getFromIndex(styleList.get(indicator)), indicator, i, i - indicator - 1);
                indicator = i;
            }
        }
        /*index.forEach(collection -> {
            logger.info(collection.toString());
        });*/
        logger.info("{}", styleList.size());
        return spansBuilder.create();
    }
}
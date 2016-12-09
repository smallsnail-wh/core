package com.dotmarketing.util;

import java.util.Iterator;
import java.util.List;

import com.dotmarketing.tag.model.Tag;

public class TagUtil {

    /**
     * Method that converts a given {@link Tag} List to a String of tag names with a CSV format
     *
     * @param tags tags to convert
     * @return CSV string with the list of tag names
     */
    public static String tagListToString(List<Tag> tags) {

        //Now we need to use the found tags in order to accrue them each time this page is visited
        if ( tags != null && !tags.isEmpty() ) {

            StringBuilder tagsPlainList = new StringBuilder();
            Iterator<Tag> tagsIterator = tags.iterator();
            while ( tagsIterator.hasNext() ) {

                Tag tag = tagsIterator.next();
                tagsPlainList.append(tag.getTagName());

                if ( tagsIterator.hasNext() ) {
                    tagsPlainList.append(",");
                }
            }

            return tagsPlainList.toString();
        }

        return "";
    }



}

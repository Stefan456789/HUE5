/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eaustria.webcrawler;

import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmayr
 */

public class LinkFinder implements Runnable {

    private String url;
    private ILinkHandler linkHandler;
    /**
     * Used fot statistics
     */
    private static final long t0 = System.nanoTime();

    public LinkFinder(String url, ILinkHandler handler) {
        // Implement Constructor
        this.url = url;
        this.linkHandler = handler;
    }

    @Override
    public void run() {
        getSimpleLinks(url);
    }

    private void getSimpleLinks(String url) {
        // 1. if crawler has not visited url yet:
        if (!linkHandler.visited(url)) {
            // 2. Create new list of recursiveActions
            List<String> urls = new ArrayList<>();
            // 3. Parse url
            try {
                System.out.println("Parsing: " + url);
                Parser p = new Parser(url);

                // 4. extract all links from url
                NodeList nodes = p.extractAllNodesThatMatch(new TagNameFilter("a"));

                var it = nodes.elements();
                while (it.hasMoreNodes()) {
                    // 5. add new Action for each sublink
                    String link = ((LinkTag) it.nextNode()).getLink();
                    if (!linkHandler.visited(link))
                        urls.add(link);
                }

            } catch (ParserException e) {
                e.printStackTrace();
            }

            linkHandler.addVisited(url);
            // 6. if size of crawler exceeds 500 -> print elapsed time for statistics
            if (linkHandler.size() > 500) {
                System.out.println(t0);
                System.out.println(linkHandler.size());
            }

            urls.forEach(x -> {
                try {
                    linkHandler.queueLink(x);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
    }
}


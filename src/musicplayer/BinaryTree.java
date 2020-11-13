package musicplayer;

import java.util.LinkedList;
/**
 * <p>
 * Binary Tree Class
 * </p>
 * @author 30021175 - Willian Bernatzki Woellner
 * @since 2020-11-08
 * @version 1.0.0
 */

public class BinaryTree {

    /**
     * Node key
     */
    Node root;

    /**
     * Node Class to implement all nodes to the tree
     */
    class Node {

        /**
         * Attributes
         */
        Song song; //Song
        Node left; //Left node position
        Node right; // right Node position

        /**
         * Node Constructor Method
         *
         * @param song Song
         */
        Node(Song song) {
            this.song = song;
            this.right = null;
            this.left = null;
        }
    }

    /**
     * AddNode Method to add a node to the tree
     *
     * @param current Node
     * @param song Song
     * @return Node added
     */
    private Node addNode(Node current, Song song) {

        //if current node not exists, it is created 
        if (current == null) {
            return new Node(song);
        }

        //Compare the title to add on the left or right of the current node
        if (song.getTitle().compareTo(current.song.getTitle()) < 0) {
            current.left = addNode(current.left, song);
        } else if (song.getTitle().compareTo(current.song.getTitle()) > 0) {
            current.right = addNode(current.right, song);
        } else {
            // node already exists not added
            return current;
        }

        return current;
    }

    /**
     * Add Method to call the AddNode Method
     *
     * @param song Song
     */
    public void add(Song song) {
        root = addNode(root, song);
    }

    /**
     * DisplayNodes Method to display all nodes in order
     *
     * @param node Node
     */
    private void displayNodes(Node node, LinkedList<Song> songs) {
        if (node != null) {
            displayNodes(node.left, songs);
            songs.addLast(node.song);
            displayNodes(node.right, songs);
        }
    }

    /**
     * Display Method to call the DisplayNodes Method
     *
     * @return
     */
    public LinkedList<Song> display() {
        LinkedList<Song> songs = new LinkedList<>();
        displayNodes(root, songs);
        return songs;
    }

    /**
     * search Method - It is used to return a song object by the title target.
     * 
     * @param title String
     * @return Song
     */    
    public Song search(String title) {
        Node n = searchNode(title, root);

        if (n != null) {
            return n.song;
        } else {
            return null;
        }
    }

    /**
     * searchNode Method - It is used to search a node by title on the binary tree.
     * @param title String
     * @param current Node
     * @return Node
     */
    private Node searchNode(String title, Node current) {
        if (current != null) {
            if (title.compareTo(current.song.getTitle()) < 0) {
                if (title.equalsIgnoreCase(current.song.getTitle())) {
                    return current;
                } else {
                    return searchNode(title, current.left);
                }
            } else {
                if (title.equalsIgnoreCase(current.song.getTitle())) {
                    return current;
                } else {
                    return searchNode(title, current.right);
                }
            }
        } else {
            return current;
        }

    }
}

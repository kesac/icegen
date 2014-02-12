package com.turtlesort.icegen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.zip.InflaterInputStream;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * An instance of this class represents a tile-based map consisting of three types of tiles:
 * floor, ice, and solid. Floor and ice tiles can be walked across, and solid tiles cannot.
 * Stepping on an ice tile keeps you in motion in the direction you stepped until you land on a floor tile
 * or collide with a solid tile. 
 */
public class IceMap {
	
	/**
	 * Tile type.
	 */
	public static enum Tile {
		ICE, FLOOR, SOLID
	};
	
	private Tile[][] map;
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	
	private String mapName;
	
	/**
	 * Creates a grid with the desired dimensions. By default, all tiles
	 * are ice tiles.
	 * @param width The desired width of the map
	 * @param height The desired height of the map
	 */
	public IceMap(int width, int height){
		this.map = new Tile[width][height];
		this.startX = 0;
		this.startY = 0;
		this.endX = 0;
		this.endY = 0;
		
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				this.map[i][j] = Tile.ICE;
			}
		}
	}
		
	/**
	 * Sets the specified tile as the starting location of a player.
	 * @param x - x-coordinate of tile
	 * @param y - y-coordinate of tile
	 */
	public void setStartTile(int x, int y){
		this.startX = x;
		this.startY = y;
	}
	
	/**
	 * Sets the specified tile as the location the player must make their way to.
	 * @param x - x-coordinate of tile
	 * @param y - y-coordinate of tile
	 */
	public void setEndTile(int x, int y){
		this.endX = x;
		this.endY = y;
	}
	
	/**
	 * Sets the tile at the specified coordinates to the given type. Only values in enum IceMap.Tile are valid types.
	 * @param x - x-coordinate of tile
	 * @param y - y-coordinate of tile
	 */
	public void setTileType(int x, int y, Tile tile){
		this.map[x][y] = tile;
	}

	/**
	 * Sets the name of this map.
	 * @param name - The desired name for the map
	 */
	public void setName(String name){
		this.mapName = name;
	}
	
	/**
	 * @param x - x-coordinate of tile
	 * @param y - y-coordinate of tile
	 * @return The type of the tile at the specified coordinates. Out of bounds locations are returned as a solid tile.
	 */
	public Tile getTileType(int x, int y){
		return isTile(x,y) ? this.map[x][y] : Tile.SOLID;
	}

	/**
	 * @return The name of this map 
	 */
	public String getName(){
		return this.mapName;
	}
	
	/**
	 * @return The x-coordinate of the starting tile
	 */
	public int getStartX() {
		return this.startX;
	}

	/**
	 * @return The y-coordinate of the starting tile
	 */
	public int getStartY() {
		return this.startY;
	}

	/**
	 * @return The x-coordinate of the tile the player must make their way to
	 */
	public int getEndX() {
		return this.endX;
	}

	/**
	 * @return The y-coordinate of the tile the player must make their way to
	 */
	public int getEndY() {
		return this.endY;
	}
	
	/**
	 * @param x - x-coordinate of the tile to test
	 * @param y - y-coordinate of the tile to test
	 * @return True if the specified tile is the starting tile, else false
	 */
	public boolean isStart(int x, int y){
		return x == this.getStartX() && y == this.getStartY();
	}
	
	/**
	 * @param x - x-coordinate of the tile to test
	 * @param y - y-coordinate of the tile to test
	 * @return True if the specified tile is the ending tile, else false
	 */
	public boolean isEnd(int x, int y){
		return x == this.getEndX() && y == this.getEndY();
	}
	
	/**
	 * @param x - x-coordinate of the tile to test
	 * @param y - y-coordinate of the tile to test
	 * @return True if the specified tile is within bounds the bounds of the map. False if the tile is out of bounds.
	 */
	public boolean isTile(int x, int y){
		return x >= 0 && x < this.getWidth() && y >= 0 && y < this.getHeight();
	}
	
	/**
	 * @return The width of the map (number of columns)
	 */
	public int getWidth(){
		return this.map.length;
	}
	
	/**
	 * @return The height of the map (number of rows)
	 */
	public int getHeight(){
		return this.map[0].length;
	}
	
	/**
	 * Parses a Tiled TMX file (needs to be saved in Base64 zlib compressed format) and returns an IceMap
	 * representing it.
	 * @param file - The file to parse
	 * @return An IceMap representing the map described in the TMX file.
	 */
	public static IceMap parseTMXFile(File file){
		
		IceMap map = null;
		
		try {
			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(file);
			
			// Attempt extraction of map dimensions
			NodeList layerNodeList = document.getElementsByTagName("layer");
			Node layerNode = layerNodeList.item(0);

			if(layerNode!= null){

				NumberFormat numberFormat = NumberFormat.getIntegerInstance();
				numberFormat.setParseIntegerOnly(true);

				NamedNodeMap attributes = layerNode.getAttributes();

				int mapWidth = numberFormat.parse(attributes.getNamedItem("width").getNodeValue()).intValue();
				int mapHeight = numberFormat.parse(attributes.getNamedItem("height").getNodeValue()).intValue();

				map = new IceMap(mapWidth, mapHeight);
				map.setName(file.getName());

			}

			// Deconstruct the data string and translate it into tile types
			if(map != null){

				NodeList dataNodeList = document.getElementsByTagName("data");
				Node dataNode = dataNodeList.item(0);

				if(dataNode != null){

					byte[] compressedData = DatatypeConverter.parseBase64Binary(dataNode.getTextContent().trim());

					ByteArrayInputStream iStream = new ByteArrayInputStream(compressedData);
					InflaterInputStream gStream = new InflaterInputStream(iStream);

					int index = 0;
					byte[] buffer = new byte[4];
					while(gStream.available() > 0){
						gStream.read(buffer);
						
						if(index >= map.getWidth()*map.getHeight()){
							break;
						}
						
						int row = index/map.getWidth();
						int col = index % map.getWidth();
						
						if(buffer[0] == 1){
							map.setTileType(col, row, IceMap.Tile.ICE);
						}
						else if(buffer[0] == 2){
							map.setTileType(col, row, IceMap.Tile.FLOOR);
						}
						else if(buffer[0] == 3){
							map.setStartTile(col, row);
						}
						else if(buffer[0] == 4){
							map.setEndTile(col, row);
						}
						else if(buffer[0] == 5){
							map.setTileType(col, row, IceMap.Tile.SOLID);
						}
						
						index++;
					}
				}
			}
			
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			
		} catch (SAXException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} catch (DOMException e) {
			e.printStackTrace();
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		return map;
	}
	
	/**
	 * Creates an IceMap based on a map created in Tiled. The file must be a JSON file to be
	 * interpreted properly. 
	 * @param file - A file object representing the file on disk to read
	 * @return An IceMap that represents the specified file, if the file was a valid Tiled JSON
	 * map. Otherwise null if the file did not exist or could not be interpreted properly
	 *//*
	public static IceMap parseTiledFile(File file){
		
		try {
			FileReader reader = new FileReader(file);
			JSONTokener tokener = new JSONTokener(reader);
			JSONObject obj = new JSONObject(tokener);

			JSONArray layers = (JSONArray)obj.get("layers");
			JSONObject layerData = (JSONObject)layers.get(0);
			JSONArray mapData = (JSONArray)layerData.get("data");

			int width = obj.getInt("width");
			int height = obj.getInt("height");

			IceMap map = new IceMap(width, height);
			map.setName(file.getName());
			
			for(int i = 0; i < width; i++){
				for(int j = 0; j < height; j++){
					
					int type = mapData.getInt(j * width + i);
					
					if(type == 1){
						map.setTileType(i, j, Tile.ICE);
					}
					else if(type == 2){
						map.setTileType(i, j, Tile.FLOOR);
					}
					else if(type == 3){
						map.setTileType(i, j, Tile.FLOOR);
						map.setStartTile(i, j);
					}
					else if(type == 4){
						map.setTileType(i, j, Tile.FLOOR);
						map.setEndTile(i, j);
					}
					else if(type == 5){
						map.setTileType(i, j, Tile.SOLID);
					}
				}
			}
			
			return map;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

	}/**/
	
}

package org.anddev.andengine.entity.layer.tiled.tmx;

import java.io.IOException;
import java.util.ArrayList;

import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.anddev.andengine.entity.layer.tiled.tmx.util.constants.TMXConstants;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXParseException;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.SAXUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

/**
 * @author Nicolas Gramlich
 * @since 19:11:29 - 20.07.2010
 */
public class TMXParser extends DefaultHandler implements TMXConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final Context mContext;
	private final TextureManager mTextureManager;
	private final ITMXTilePropertiesListener mTMXTilePropertyListener;
	private final TextureOptions mTextureOptions;

	private TMXTiledMap mTMXTiledMap;

	private int mLastTileSetTileID;

	private final StringBuilder mStringBuilder = new StringBuilder();

	@SuppressWarnings("unused")
	private boolean mInMap;
	@SuppressWarnings("unused")
	private boolean mInTileset;
	@SuppressWarnings("unused")
	private boolean mInImage;
	private boolean mInTile;
	@SuppressWarnings("unused")
	private boolean mInProperties;
	@SuppressWarnings("unused")
	private boolean mInProperty;
	@SuppressWarnings("unused")
	private boolean mInLayer;
	@SuppressWarnings("unused")
	private boolean mInData;
	@SuppressWarnings("unused")
	private boolean mInObjectGroup;
	private boolean mInObject;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TMXParser(final Context pContext, final TextureManager pTextureManager, final TextureOptions pTextureOptions, final ITMXTilePropertiesListener pTMXTilePropertyListener) {
		this.mContext = pContext;
		this.mTextureManager = pTextureManager;
		this.mTextureOptions = pTextureOptions;
		this.mTMXTilePropertyListener = pTMXTilePropertyListener;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	TMXTiledMap getTMXTiledMap() {
		return this.mTMXTiledMap;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void startElement(final String pUri, final String pLocalName, final String pQualifiedName, final Attributes pAttributes) throws SAXException {
		if(pLocalName.equals(TAG_MAP)){
			this.mInMap = true;
			this.mTMXTiledMap = new TMXTiledMap(pAttributes);
		} else if(pLocalName.equals(TAG_TILESET)){
			this.mInTileset = true;
			new TMXTileSet(pAttributes, this.mTextureOptions).addTMXTileSet(this.mTMXTiledMap);
		} else if(pLocalName.equals(TAG_IMAGE)){
			this.mInImage = true;
			final ArrayList<TMXTileSet> tmxTileSets = this.mTMXTiledMap.getTMXTileSets();
			tmxTileSets.get(tmxTileSets.size() - 1).setImageSource(this.mContext, this.mTextureManager, pAttributes);
		} else if(pLocalName.equals(TAG_TILE)) {
			this.mInTile = true;
			this.mLastTileSetTileID = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_TILE_ATTRIBUTE_ID);
		} else if(pLocalName.equals(TAG_PROPERTIES)) {
			this.mInProperties = true;
		} else if(pLocalName.equals(TAG_PROPERTY)) {
			this.mInProperty = true;
			if(this.mInTile) {
				final ArrayList<TMXTileSet> tmxTileSets = this.mTMXTiledMap.getTMXTileSets();
				tmxTileSets.get(tmxTileSets.size() - 1).addTMXTileProperty(this.mLastTileSetTileID, new TMXTileProperty(pAttributes));
			} else if (this.mInObject) {
				final ArrayList<TMXObjectGroup> tmxObjectGroups = this.mTMXTiledMap.getTMXObjectGroups();
				final TMXObjectGroup lastObjectGroup = tmxObjectGroups.get(tmxObjectGroups.size() - 1);

				final ArrayList<TMXObject> tmxObjects = lastObjectGroup.getTMXObjects();
				tmxObjects.get(tmxObjects.size() - 1).addTMXObjectProperty(new TMXObjectProperty(pAttributes));
			}
		} else if(pLocalName.equals(TAG_LAYER)){
			this.mInLayer = true;
			new TMXLayer(this.mTMXTiledMap, pAttributes).addTMXLayer(this.mTMXTiledMap);
		} else if(pLocalName.equals(TAG_DATA)){
			this.mInData = true;
		} else if(pLocalName.equals(TAG_OBJECTGROUP)){
			this.mInObjectGroup = true;
			this.mTMXTiledMap.addTMXObjectGroup(new TMXObjectGroup(pAttributes));
		} else if(pLocalName.equals(TAG_OBJECT)){
			this.mInObject = true;
			final ArrayList<TMXObjectGroup> tmxObjectGroups = this.mTMXTiledMap.getTMXObjectGroups();
			tmxObjectGroups.get(tmxObjectGroups.size() - 1).addTMXObject(new TMXObject(pAttributes));
		} else {
			throw new TMXParseException("Unexpected start tag: '" + pLocalName + "'.");
		}
	}

	@Override
	public void characters(final char[] pCharacters, final int pStart, final int pLength) throws SAXException {
		this.mStringBuilder.append(pCharacters, pStart, pLength);
	}

	@Override
	public void endElement(final String pUri, final String pLocalName, final String pQualifiedName) throws SAXException {
		if(pLocalName.equals(TAG_MAP)){
			this.mInMap = false;
		} else if(pLocalName.equals(TAG_TILESET)){
			this.mInTileset = false;
		} else if(pLocalName.equals(TAG_IMAGE)){
			this.mInImage = false;
		} else if(pLocalName.equals(TAG_TILE)) {
			this.mInTile = false;
		} else if(pLocalName.equals(TAG_PROPERTIES)) {
			this.mInProperties = false;
		} else if(pLocalName.equals(TAG_PROPERTY)) {
			this.mInProperty = false;
		} else if(pLocalName.equals(TAG_LAYER)){
			this.mInLayer = false;
		} else if(pLocalName.equals(TAG_DATA)){
			final ArrayList<TMXLayer> tmxLayers = this.mTMXTiledMap.getTMXLayers();
			try {
				tmxLayers.get(tmxLayers.size() - 1).mTMXTiledMap.initializeTMXTiles(tmxLayers.get(tmxLayers.size() - 1), this.mStringBuilder.toString().trim(), this.mTMXTilePropertyListener);
			} catch (final IOException e) {
				Debug.e(e);
			}
			this.mInData = false;
		} else if(pLocalName.equals(TAG_OBJECTGROUP)){
			this.mInObjectGroup = false;
		} else if(pLocalName.equals(TAG_OBJECT)){
			this.mInObject = false;
		} else {
			throw new TMXParseException("Unexpected end tag: '" + pLocalName + "'.");
		}

		/* Reset the StringBuilder. */
		this.mStringBuilder.setLength(0);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

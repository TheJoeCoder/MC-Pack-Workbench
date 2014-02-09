package net.mightypork.rpw.tree.filesystem;


import java.io.File;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.utils.FileUtils;


/**
 * File filesystem tree node
 * 
 * @author MightyPork
 */
public class FileFsTreeNode extends AbstractFsTreeNode {

	private String name;
	private EAsset type;


	public FileFsTreeNode(File path) {

		this.path = path;
		this.name = FileUtils.getBasename(path.getName());
		this.type = EAsset.forFile(path);
	}


	@Override
	public Enumeration children() {

		return null;
	}


	@Override
	public AbstractFsTreeNode getChildAt(int childIndex) {

		return null;
	}


	@Override
	public int getChildCount() {

		return 0;
	}


	@Override
	public int getIndex(TreeNode node) {

		return -1;
	}


	/**
	 * Get file path
	 * 
	 * @return path
	 */
	@Override
	public File getPath() {

		return path;
	}


	@Override
	public String getName() {

		return name;
	}


	@Override
	public boolean isDirectory() {

		return false;
	}


	@Override
	public boolean isFile() {

		return true;
	}


	@Override
	public boolean isSound() {

		return type.isSound();
	}


	@Override
	public boolean isImage() {

		return type.isImage();
	}


	@Override
	public boolean isText() {

		return type.isText();
	}


	@Override
	public boolean isJson() {

		return type.isText();
	}

}
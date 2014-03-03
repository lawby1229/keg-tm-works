package edu.thu.keg.GB.http0702.brand.features.tfidf;

import java.io.ObjectInputStream.GetField;
import java.sql.ResultSet;

public interface IBrandTools {
	public ResultSet getRs(String tableName, String tag);

	public void getFile(boolean isTrain);
}

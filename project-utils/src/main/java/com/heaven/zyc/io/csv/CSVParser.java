/**
 * @(#)CSVParser.java
 *
 * Copyright 2011 naryou, Inc. All rights reserved.
 */
package com.heaven.zyc.io.csv;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.heaven.zyc.io.IOUtils;
import com.heaven.zyc.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CSV文件解析器
 * @author  jianguo.xu
 * @version 1.0,2011-10-21
 */
public class CSVParser {
	
	private static final Log LOG = LogFactory.getLog(CSVParser.class);
	
	/**
	 * CSV表格
	 * 
	 * @author tiger
	 * 
	 */
	public static class CSV {
		private List<String> header = new ArrayList<String>();
		private List<List<String>> body = new ArrayList<List<String>>();

		private CSV() {
			super();
		}

		private CSV(List<String> header, List<List<String>> body) {
			 
			this.header = header;
			this.body = body;
		}

		public List<String> getHeader() {
			return header;
		}

		public List<List<String>> getBody() {
			return body;
		}

		public void setHeader(List<String> header) {
			this.header = header;
		}
		public void setBody(List<List<String>> body) {
			this.body = body;
		}
	}
	private int maxColumnCount = 0;
	private CSVParser() {
		 
	}

	public static CSVParser newInstance() {
		return new CSVParser();
	}
	
	public CSV parseCSV(File csvFile,boolean existHead) {
		return parseCSV(csvFile, existHead, null);
	}
	
	public CSV parseCSV(File csvFile,boolean existHead,List<CSVValidator> validators) {
		String content = IOUtils.fileToString(csvFile);
		return parseCSV(content, existHead, validators);
	}
	
	public CSV parseCSV(InputStream ins,boolean existHead) {
		return parseCSV(ins, existHead, null);
	}
	
	public CSV parseCSV(InputStream ins,boolean existHead,List<CSVValidator> validators) {
		String content = IOUtils.inputStreamToString(ins);
		return parseCSV(content, existHead, validators);
	}
	
	private CSV parseCSV(String content,boolean existHead,List<CSVValidator> validators){
		CSV csv = new CSV();
		if(LOG.isDebugEnabled()) LOG.debug(content);
		validatorCsvNumberFormat(content);
		if(StringUtils.isNullOrEmpty(content))return csv;
		List<List<String>> list= convertToList(content);
		if(existHead) {
			csv.setHeader(list.remove(0));
		}
		csv.setBody(list);
		validator(list, existHead, validators);
		return csv;
	}
 
	private void validatorCsvNumberFormat(String content) {
		if(content.indexOf("E+")!=-1) {
			throw new RuntimeException("CSV文件中不允许包含数字的缩写:E+，请调整单元格设置并保存");
		}
	}
	
	/**
	 * 根据验证规则验证CSV文件
	 * @author jianguo.xu
	 * @param body
	 * @param validators
	 */
	private void validator(List<List<String>> body,boolean existHead,List<CSVValidator> validators) {
		if(validators==null||validators.size() == 0) return;
		for(int rowIndex = 0;rowIndex<body.size();rowIndex++) {
			List<String> rowList = body.get(rowIndex);
			for(int columnIndex = 0;columnIndex<rowList.size();columnIndex++) {
				validator(rowList.get(columnIndex), validators, rowIndex+(existHead?1:0),columnIndex);
			}
		}
	}
	
	private void validator(String value,List<CSVValidator> validators,int rowIndex,int columnIndex) {
		for(CSVValidator cvsValidator : validators) {
			if(cvsValidator.getIndex() == columnIndex) {
				validatorRequired(value, cvsValidator, rowIndex+1, columnIndex+1);
				if(StringUtils.isNullOrEmpty(value))return;
				validatorLength(value, cvsValidator, rowIndex+1, columnIndex+1);
				validatorMinAndMax(value, cvsValidator, rowIndex+1, columnIndex+1);
				validatorType(value, cvsValidator, rowIndex+1, columnIndex+1);
			}
		}
	}
	/**
	 * 必填验证
	 * @author jianguo.xu
	 * @param value
	 * @param cvsValidator
	 * @param rowCount
	 * @param columnCount
	 */
	private void validatorRequired(String value,CSVValidator cvsValidator,int rowCount,int columnCount) {
		if(StringUtils.isNullOrEmpty(value)&&cvsValidator.isRequired()) {
			throw new RuntimeException("第 "+rowCount+"行, 第 "+columnCount+"列必填");
		}
	}
	
	/**
	 * 对长度进行验证
	 * @author jianguo.xu
	 * @param value
	 * @param rowCount
	 * @param columnCount
	 */
	private void validatorLength(String value,CSVValidator cvsValidator,int rowCount,int columnCount) {
		int[] len = cvsValidator.getLen();
		if(len==null||len.length == 0) return;
		if(len.length == 1)  {
			if(value.length()!=len[0])
				throw new RuntimeException("第 "+rowCount+"行, 第 "+columnCount+"列的长度必须为 "+len[0]); 
		}
		else {
			if(value.length()<len[0]||value.length()>len[1]) {
				throw new RuntimeException("第 "+rowCount+"行, 第 "+columnCount+"列的长度必须在  "+len[0]+" 和  "+len[1]+" 之间"); 
			}
		}
		
	}
	
	/**
	 * 对数字进行最大值和最小值验证
	 * @author jianguo.xu
	 * @param value
	 */
	private void validatorMinAndMax(String value,CSVValidator cvsValidator,int rowCount,int columnCount) {
		if(cvsValidator.getMinValue()==null&&cvsValidator.getMaxValue()==null) return;
		int intValue = 0;
		try {
			intValue = Integer.parseInt(value);
		} catch (Exception e) {
			throw new RuntimeException("第 "+rowCount+"行, 第 "+columnCount+"列必须为数字");
		}
		if(cvsValidator.getMinValue()!=null&&intValue<cvsValidator.getMinValue()) {
			throw new RuntimeException("第 "+rowCount+"行, 第 "+columnCount+"列不能小于 "+cvsValidator.getMinValue());
		}
		if(cvsValidator.getMaxValue()!=null&&intValue>cvsValidator.getMaxValue()) {
			throw new RuntimeException("第 "+rowCount+"行, 第 "+columnCount+"列不能大于于 "+cvsValidator.getMaxValue());
		}
	}
	
	private void validatorType(String value,CSVValidator cvsValidator,int rowCount,int columnCount) {
		CSVValidatorType[]  validatorTypes = cvsValidator.getVaildatorTypes();
		 if(validatorTypes == null) return;
		 for(CSVValidatorType validatorType : validatorTypes) {
			 if(!validatorType.validator(value)) {
				 throw new RuntimeException("第 "+rowCount+"行, 第 "+columnCount+"数据出错： "+validatorType.getErrorMsg(value));
				}
		 }
		 
	}
	
	
	private List<List<String>> convertToList(String content) {
		List<List<String>> csvList = new ArrayList<List<String>>();
		String[] rowArrays = content.split("\r\n");
		for(String rowStr: rowArrays) {
			String[] cellArrays =  rowStr.split(",");
			setMaxColumnCount(cellArrays);
			csvList.add(new ArrayList<String>(Arrays.asList(cellArrays)));
		}
		fillCSVList(csvList);
		 
		return csvList;
	}
	
	private void setMaxColumnCount(String[] cellArrays) {
		this.maxColumnCount = this.maxColumnCount>=cellArrays.length?this.maxColumnCount:cellArrays.length;
	}
	/**
	 * 填充CSV的列集合<br/>
	 * 以最大列数量
	 * @author jianguo.xu
	 * @param csvList
	 */
	private void fillCSVList(List<List<String>> csvList) {
		for(List<String> columnList : csvList) {
			if(columnList.size()<this.maxColumnCount)
				fillNullObj(columnList, this.maxColumnCount-columnList.size());
		}
	}
	
	private void fillNullObj(List<String> columnList,int count) {
		 for(int i = 0;i<count;i++) {
			 columnList.add(null);
		 }
	}
	
/*	
	
	public static void main(String[] args) {
		CSVValidator v0 = new CSVValidator(0);
		v0.setRequired(true);
		v0.setLen(new int[]{11});
		v0.setVaildatorTypes(new CSVValidatorType[]{CSVValidatorType.MOBILE});
		
		CSVValidator v3 = new CSVValidator(3);
		v3.setRequired(false);
		v3.setVaildatorTypes(new CSVValidatorType[]{CSVValidatorType.DATE});
		
		CSVValidator v8 = new CSVValidator(8);
		v8.setRequired(true);
		v8.setMaxValue(1234);
		
		List<CSVValidator> cSVValidators= new ArrayList<CSVValidator>();
		cSVValidators.add(v0);
		cSVValidators.add(v3);
		cSVValidators.add(v8);
		File file  = new File("d:/temp2/member.csv");
		CSV csv = CSVParser.newInstance().parseCSV(file, true,cSVValidators);
		List<String> header = csv.getHeader();
		System.out.print(header.size()+"\t");
		for(String str : header) {
			System.out.print(str+"\t\t");
		}
		System.out.println();
		List<List<String>> body = csv.getBody();
		for(List<String> rows : body) {
			System.out.print(rows.size()+"\t");
			for(String str : rows) {
				System.out.print(str+"|\t\t");
			}
			System.out.println();
		}
	}*/
}

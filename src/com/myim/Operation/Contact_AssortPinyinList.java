package com.myim.Operation;


import net.sourceforge.pinyin4j.PinyinHelper;
import com.myim.sort.HashList;
import com.myim.sort.KeySort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Contact_AssortPinyinList {

	private HashList<String,String> hashList=new HashList<String,String>(new KeySort<String,String>(){
		public String getKey(String value) {
			return getFirstChar(value);
		}});

	        //����ַ���������ĸ ���ַ� ת����ƴ��
		public  String getFirstChar(String value) {
			// ���ַ�
			char firstChar = value.charAt(0);
			// ����ĸ����
			String first = null;
			// �Ƿ��� �Ǻ���
			String[] print = PinyinHelper.toHanyuPinyinStringArray(firstChar);

			if (print == null) {

				// ��Сд��ĸ�ĳɴ�д
				if ((firstChar >= 97 && firstChar <= 122)) {
					firstChar -= 32;
				}
				if (firstChar >= 65 && firstChar <= 90) {
					first = String.valueOf((char) firstChar);
				} else {
					// ��Ϊ���ַ�Ϊ���ֻ��������ַ�
					first = "#";
				}
			} else {
				// ��������� �����д��ĸ
				first = String.valueOf((char)(print[0].charAt(0) -32));
			}
			if (first == null) {
				first = "*";
			}
			return first;
		}

		public HashList<String, String> getHashList() {
			return hashList;
		}
		public ArrayList<String> getArrayList()
		{
			ArrayList<String> list = new ArrayList<String>();
			//list = (ArrayList<String>) hashList.subList(0,hashList.size());
			HashMap<String, List<String>> map = hashList.getMap();

			return list;
		}
		

}

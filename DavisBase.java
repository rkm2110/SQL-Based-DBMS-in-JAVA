
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.RandomAccessFile;
import java.util.Date;
import java.text.SimpleDateFormat;



public class DavisBase 
{
        
    static Scanner sc = new Scanner(System.in).useDelimiter(";");
    public static int page_size = 512;
    static boolean is_exit = false;
	
	static String prompt = "RKsql> ";
	static String dirCatalog = "data/catalog";
	static String dirUsrData = "data/user_data";
	
	public static void main(String[] args) 
	{
        splashScreen();
        initialize();
		String usrCmd = ""; 

		while(!is_exit) 
		{
			System.out.print(prompt);
			usrCmd = sc.next().replace("\n", " ").replace("\r", "").trim().toLowerCase();
			parseUsrCmd(usrCmd);
		}
		System.out.println("Exiting...");
	}
	
	
    public static void splashScreen() 
	{
		System.out.println(line("-",80));
		System.out.println();
        System.out.println("Welcome to DavisBase."); // Display the string.
        System.out.println("\nType \"help;\" to display supported commands.");
		System.out.println("Use \";\" at the end of each command.");
		System.out.println();
		System.out.println(line("-",80));
		System.out.println();
	}
	
	
	
	public static void help() 
	{
		System.out.println();
		System.out.println(line("*",80));
		System.out.println();
		System.out.println("SUPPORTED COMMANDS :");
		System.out.println();
		System.out.println("SHOW TABLES;\n\tDisplay all the tables in the database.");
		System.out.println("\nCREATE TABLE table_name (<column_name datatype> <NOT NULL/UNIQUE>); \n\tCreate new table in the database.");
		System.out.println("\nDROP TABLE table_name;\n\tRemove table.");
		System.out.println("\nINSERT INTO table_name VALUES (value1,value2,....);\n\tInsert new row into table. Column1 is always primary key \n\twhich is inbuilt and increments automatically.");
		System.out.println("\nDELETE FROM TABLE table_name WHERE row_id = key_value;\n\tDelete a record from the table whose rowid is <key_value>.");
		System.out.println("\nUPDATE table_name SET column_name = value WHERE condition;\n\tModifies the records in the table.");
		System.out.println("\nSELECT * FROM table_name;\n\tDisplay all records from table.");
		System.out.println("\nSELECT * FROM table_name WHERE column_name operator value; \n\tDisplay records from table where given condition is satisfied.");
		System.out.println("\nHELP;\n\tShow supported commands.");
		System.out.println("\nEXIT;\n\tExit program.");
		System.out.println();
		System.out.println(line("*",80));
		System.out.println();
	}
	
	//To print a line of a character (-) or (*)
	//Like : --------------------------------------------------------
	public static String line(String s,int num) 
	{
		String a = "";
		for(int t=0;t<num;t++) 
		{
			a += s;
		}
		return a;
	}
	

	
	//initialize the folders and files to be written
	public static void initialize()
	{
		try 
		{
			File data_dir = new File("data");
			if(data_dir.mkdir())
			{
				initialize2();
			}
			else 
			{
				data_dir = new File(dirCatalog);
				String[] old_table_files = data_dir.list();
				boolean checkTab = false;
				boolean checkCol = false;
				for (int t=0; t<old_table_files.length; t++) 
				{
					if(old_table_files[t].equals("davisbase_tables.tbl"))
						checkTab = true;
					if(old_table_files[t].equals("davisbase_columns.tbl"))
						checkCol = true;
				}
				
				if(!checkTab)
				{
					initialize2();
				}
				
				if(!checkCol)
				{
					initialize2();
				}
				
			}
		}
		catch (SecurityException e) 
		{
			System.out.println(e);
		}
	}
	
	
	
	public static boolean table_exists(String table_name)
	{
		table_name = table_name+".tbl";
		
		try 
		{
			File data_dir = new File(dirUsrData);
			if (table_name.equalsIgnoreCase("davisbase_tables.tbl") || table_name.equalsIgnoreCase("davisbase_columns.tbl"))
				data_dir = new File(dirCatalog) ;
			
			String[] old_table_files;
			old_table_files = data_dir.list();
			for (int t=0; t<old_table_files.length; t++) 
			{
				if(old_table_files[t].equals(table_name))
					return true;
			}
		}
		catch (Exception e) 
		{
			System.out.println("Data container directory creation failed");
			System.out.println(e);
		}

		return false;
	}
	


	public static String[] parserEqn(String eqn)
	{
		String cmpartr[] = new String[3];
		String tmp[] = new String[2];
		
		
		if(eqn.contains("<")) 
		{
			tmp = eqn.split("<");
			cmpartr[0] = tmp[0].trim();
			cmpartr[1] = "<";
			cmpartr[2] = tmp[1].trim();
		}
		if(eqn.contains("=")) 
		{
			tmp = eqn.split("=");
			cmpartr[0] = tmp[0].trim();
			cmpartr[1] = "=";
			cmpartr[2] = tmp[1].trim();
		}
		if(eqn.contains(">")) 
		{
			tmp = eqn.split(">");
			cmpartr[0] = tmp[0].trim();
			cmpartr[1] = ">";
			cmpartr[2] = tmp[1].trim();
		}
		if(eqn.contains("!=")) 
		{
			tmp = eqn.split("!=");
			cmpartr[0] = tmp[0].trim();
			cmpartr[1] = "!=";
			cmpartr[2] = tmp[1].trim();
		}

		
		if(eqn.contains(">=")) 
		{
			tmp = eqn.split(">=");
			cmpartr[0] = tmp[0].trim();
			cmpartr[1] = ">=";
			cmpartr[2] = tmp[1].trim();
		}
		if(eqn.contains("<=")) 
		{
			tmp = eqn.split("<=");
			cmpartr[0] = tmp[0].trim();
			cmpartr[1] = "<=";
			cmpartr[2] = tmp[1].trim();
		}

		
		return cmpartr;
	}

        
    public static void initialize2() 
	{
		try 
		{
			File data_dir = new File(dirUsrData);
			data_dir.mkdir();
			data_dir = new File(dirCatalog);
			data_dir.mkdir();
			String[] old_table_files;
			old_table_files = data_dir.list();
			for (int t=0; t<old_table_files.length; t++) 
			{
				File old_file = new File(data_dir, old_table_files[t]); 
				old_file.delete();
			}
		}
		catch (SecurityException e) 
		{
			System.out.println(e);
		}

		try 
		{
			RandomAccessFile tables_catalog = new RandomAccessFile(dirCatalog+"/davisbase_tables.tbl", "rw");
			tables_catalog.setLength(page_size);
			tables_catalog.seek(0);
			tables_catalog.write(0x0D);
			tables_catalog.writeByte(0x02);
			
			int size_1=24;
			int size_2=25;
			
			int off_set_T=page_size-size_1;
			int off_set_C=off_set_T-size_2;
			
			tables_catalog.writeShort(off_set_C);
			tables_catalog.writeInt(0);
			tables_catalog.writeInt(0);
			tables_catalog.writeShort(off_set_T);
			tables_catalog.writeShort(off_set_C);
			
			tables_catalog.seek(off_set_T);
			tables_catalog.writeShort(20);
			tables_catalog.writeInt(1); 
			tables_catalog.writeByte(1);
			tables_catalog.writeByte(28);
			tables_catalog.writeBytes("davisbase_tables");
			
			tables_catalog.seek(off_set_C);
			tables_catalog.writeShort(21);
			tables_catalog.writeInt(2); 
			tables_catalog.writeByte(1);
			tables_catalog.writeByte(29);
			tables_catalog.writeBytes("davisbase_columns");
			
			tables_catalog.close();
		}
		catch (Exception e) 
		{
			System.out.println(e);
		}
		
		try 
		{
			RandomAccessFile columns_catalog = new RandomAccessFile(dirCatalog+"/davisbase_columns.tbl", "rw");
			columns_catalog.setLength(page_size);
			columns_catalog.seek(0);       
			columns_catalog.writeByte(0x0D); 
			columns_catalog.writeByte(0x09); //no of records
			
			int[] off_set=new int[9];
			off_set[0]=page_size-45;
			off_set[1]=off_set[0]-49;
			off_set[2]=off_set[1]-46;
			off_set[3]=off_set[2]-50;
			off_set[4]=off_set[3]-51;
			off_set[5]=off_set[4]-49;
			off_set[6]=off_set[5]-59;
			off_set[7]=off_set[6]-51;
			off_set[8]=off_set[7]-49;
			
			columns_catalog.writeShort(off_set[8]); 
			columns_catalog.writeInt(0); 
			columns_catalog.writeInt(0); 
			
			for(int t=0;t<off_set.length;t++)
				columns_catalog.writeShort(off_set[t]);

			
			columns_catalog.seek(off_set[0]);
			columns_catalog.writeShort(36);
			columns_catalog.writeInt(1); 
			columns_catalog.writeByte(6);
			columns_catalog.writeByte(28);
			columns_catalog.writeByte(17);
			columns_catalog.writeByte(15);
			columns_catalog.writeByte(4);
			columns_catalog.writeByte(14);
			columns_catalog.writeByte(14);
			columns_catalog.writeBytes("davisbase_tables"); 
			columns_catalog.writeBytes("rowid"); 
			columns_catalog.writeBytes("INT"); 
			columns_catalog.writeByte(1); 
			columns_catalog.writeBytes("NO"); 
			columns_catalog.writeBytes("NO"); 
			columns_catalog.writeBytes("NO");
			
			columns_catalog.seek(off_set[1]);
			columns_catalog.writeShort(42); 
			columns_catalog.writeInt(2); 
			columns_catalog.writeByte(6);
			columns_catalog.writeByte(28);
			columns_catalog.writeByte(22);
			columns_catalog.writeByte(16);
			columns_catalog.writeByte(4);
			columns_catalog.writeByte(14);
			columns_catalog.writeByte(14);
			columns_catalog.writeBytes("davisbase_tables"); 
			columns_catalog.writeBytes("table_name"); 
			columns_catalog.writeBytes("TEXT"); 
			columns_catalog.writeByte(2);
			columns_catalog.writeBytes("NO"); 
			columns_catalog.writeBytes("NO");
			
			columns_catalog.seek(off_set[2]);
			columns_catalog.writeShort(37); 
			columns_catalog.writeInt(3); 
			columns_catalog.writeByte(6);
			columns_catalog.writeByte(29);
			columns_catalog.writeByte(17);
			columns_catalog.writeByte(15);
			columns_catalog.writeByte(4);
			columns_catalog.writeByte(14);
			columns_catalog.writeByte(14);
			columns_catalog.writeBytes("davisbase_columns");
			columns_catalog.writeBytes("rowid");
			columns_catalog.writeBytes("INT");
			columns_catalog.writeByte(1);
			columns_catalog.writeBytes("NO");
			columns_catalog.writeBytes("NO");
			
			columns_catalog.seek(off_set[3]);
			columns_catalog.writeShort(43);
			columns_catalog.writeInt(4); 
			columns_catalog.writeByte(6);
			columns_catalog.writeByte(29);
			columns_catalog.writeByte(22);
			columns_catalog.writeByte(16);
			columns_catalog.writeByte(4);
			columns_catalog.writeByte(14);
			columns_catalog.writeByte(14);
			columns_catalog.writeBytes("davisbase_columns");
			columns_catalog.writeBytes("table_name");
			columns_catalog.writeBytes("TEXT");
			columns_catalog.writeByte(2);
			columns_catalog.writeBytes("NO");
			columns_catalog.writeBytes("NO");
			
			columns_catalog.seek(off_set[4]);
			columns_catalog.writeShort(44);
			columns_catalog.writeInt(5); 
			columns_catalog.writeByte(6);
			columns_catalog.writeByte(29);
			columns_catalog.writeByte(23);
			columns_catalog.writeByte(16);
			columns_catalog.writeByte(4);
			columns_catalog.writeByte(14);
			columns_catalog.writeByte(14);
			columns_catalog.writeBytes("davisbase_columns");
			columns_catalog.writeBytes("column_name");
			columns_catalog.writeBytes("TEXT");
			columns_catalog.writeByte(3);
			columns_catalog.writeBytes("NO");
			columns_catalog.writeBytes("NO");
			
			columns_catalog.seek(off_set[5]);
			columns_catalog.writeShort(42);
			columns_catalog.writeInt(6); 
			columns_catalog.writeByte(6);
			columns_catalog.writeByte(29);
			columns_catalog.writeByte(21);
			columns_catalog.writeByte(16);
			columns_catalog.writeByte(4);
			columns_catalog.writeByte(14);
			columns_catalog.writeByte(14);
			columns_catalog.writeBytes("davisbase_columns");
			columns_catalog.writeBytes("data_type");
			columns_catalog.writeBytes("TEXT");
			columns_catalog.writeByte(4);
			columns_catalog.writeBytes("NO");
			columns_catalog.writeBytes("NO");
			
			columns_catalog.seek(off_set[6]);
			columns_catalog.writeShort(52); 
			columns_catalog.writeInt(7); 
			columns_catalog.writeByte(6);
			columns_catalog.writeByte(29);
			columns_catalog.writeByte(28);
			columns_catalog.writeByte(19);
			columns_catalog.writeByte(4);
			columns_catalog.writeByte(14);
			columns_catalog.writeByte(14);
			columns_catalog.writeBytes("davisbase_columns");
			columns_catalog.writeBytes("ordinal_position");
			columns_catalog.writeBytes("TINYINT");
			columns_catalog.writeByte(5);
			columns_catalog.writeBytes("NO");
			columns_catalog.writeBytes("NO");
			
			columns_catalog.seek(off_set[7]);
			columns_catalog.writeShort(44); 
			columns_catalog.writeInt(8); 
			columns_catalog.writeByte(6);
			columns_catalog.writeByte(29);
			columns_catalog.writeByte(23);
			columns_catalog.writeByte(16);
			columns_catalog.writeByte(4);
			columns_catalog.writeByte(14);
			columns_catalog.writeByte(14);
			columns_catalog.writeBytes("davisbase_columns");
			columns_catalog.writeBytes("is_nullable");
			columns_catalog.writeBytes("TEXT");
			columns_catalog.writeByte(6);
			columns_catalog.writeBytes("NO");
			columns_catalog.writeBytes("NO");
		

			columns_catalog.seek(off_set[8]);
			columns_catalog.writeShort(42); 
			columns_catalog.writeInt(9); 
			columns_catalog.writeByte(6);
			columns_catalog.writeByte(29);
			columns_catalog.writeByte(21);
			columns_catalog.writeByte(16);
			columns_catalog.writeByte(4);
			columns_catalog.writeByte(14);
			columns_catalog.writeByte(14);
			columns_catalog.writeBytes("davisbase_columns");
			columns_catalog.writeBytes("is_unique");
			columns_catalog.writeBytes("TEXT");
			columns_catalog.writeByte(7);
			columns_catalog.writeBytes("NO");
			columns_catalog.writeBytes("NO");
			
			columns_catalog.close();
		}
		catch (Exception e) 
		{
			System.out.println(e);
		}
	}


    //Parse User input command to separate commands   
	public static void parseUsrCmd (String usrCmd) 
	{
		
		ArrayList<String> cmdTokens = new ArrayList<String>(Arrays.asList(usrCmd.split(" ")));

		switch (cmdTokens.get(0)) 
		{

		    	
		    case "create":
				switch (cmdTokens.get(1)) 
				{
					case "table": 
						parse_create(usrCmd);
						break;
						
					default:
						System.out.println("Wrong command : \"" + usrCmd + "\"");
						System.out.println();
						break;
				}
				break;
                        
                
			case "show":
			    show_tables();
			    break;
		
            case "select":
				parse_query(usrCmd);
				break;

			    
			case "insert":
				parse_insert(usrCmd);
				break;
				
			case "drop":
				drop_table(usrCmd);
				break;	
            
			case "quit":
				is_exit=true;
				break;
	
			case "exit":
				is_exit=true;
				break;
			        
			case "help":
				help();
				break;
				
            default:
				System.out.println("wrong syntax: \"" + usrCmd + "\"");
				System.out.println();
				break;
		}
	} 

	
    public static void parse_create(String create_string) 
	{	
		String[] tokens=create_string.split(" ");
		String table_name = tokens[2];
		String[] tmp = create_string.split(table_name);
		String cols = tmp[1].trim();
		String[] create_cols = cols.substring(1, cols.length()-1).split(",");
		
		for(int t = 0; t < create_cols.length; t++)
			create_cols[t] = create_cols[t].trim();
		
		if(table_exists(table_name))
		{
			System.out.println("Table "+table_name+" already exists.");
		}
		else
		{
			UserTable.create_table(table_name, create_cols);		
		}

	}
	
	public static void show_tables() 
	{
		String table = "davisbase_tables";
		String[] cols = {"table_name"};
		String[] cmptr = new String[0];
		UserTable.select(table, cols, cmptr,dirUsrData+"/");
	}
    
    public static void parse_insert(String insert_string) 
	{
    	try
		{
			String[] tokens=insert_string.split(" ");
			String table = tokens[2];
			String[] tmp = insert_string.split("values");
			String temp=tmp[1].trim();
			String[] insertVals = temp.substring(1, temp.length()-1).split(",");
			for(int t = 0; t < insertVals.length; t++)
				insertVals[t] = insertVals[t].trim();
		
			if(!table_exists(table)){
				System.out.println("Table "+table+" unavailable.");
			}
			else
			{
				UserTable.insert_into(table, insertVals,dirUsrData+"/");
			}
    	}
    	catch(Exception e)
    	{
    		System.out.println(e+e.toString());
    	}

	}
    public static void drop_table(String drop_table_string) 
	{
		String[] tokens=drop_table_string.split(" ");
		String table_name = tokens[2];
		if(!table_exists(table_name))
		{
			System.out.println("Table "+table_name+" unavailable.");
		}
		else
		{
			UserTable.drop(table_name);
		}		

	}
	
    public static void parse_query(String query_string) 
	{
		String[] comp;
		String[] col;
		String[] tmp = query_string.split("where");
		if(tmp.length > 1)
		{
			String tmp2 = tmp[1].trim();
			comp = parserEqn(tmp2);
		}
		else
		{
			comp = new String[0];
		}
		String[] select = tmp[0].split("from");
		String table_name = select[1].trim();
		String cols = select[0].replace("select", "").trim();
		if(cols.contains("*"))
		{
			col = new String[1];
			col[0] = "*";
		}
		else
		{
			col = cols.split(",");
			for(int t = 0; t < col.length; t++)
				col[t] = col[t].trim();
		}
		
		if(!table_exists(table_name)){
			System.out.println("Table "+table_name+" not available.");
		}
		else
		{
		    UserTable.select(table_name, col, comp,dirUsrData+"/");
		}
	}
	
	

}



class UserTable
{
	
	public static String dirCatalog = "data/catalog/";
	public static String dirUsrData = "data/user_data/";
	public static String date_pattern = "yyyy-MM-dd_HH:mm:ss";
	public static int page_size = 512;
	public static int numOfRecords;
	
	
	public static void drop(String table)
	{
		try
		{
			RandomAccessFile file = new RandomAccessFile(dirCatalog+"davisbase_tables.tbl", "rw");
			int num_of_pages = pages(file);
			for(int page = 1; page <= num_of_pages; page ++)
			{
				file.seek((page-1)*page_size);
				byte file_type = file.readByte();
				if(file_type == 0x0D)
				{   
					short[] cell_addr = Tree.get_cell_array(file, page);
					int k = 0;
					for(int t = 0; t < cell_addr.length; t++)
					{   
						long position = Tree.get_cell_position(file, page, t);
						String[] vals = retrieve_values(file, position);
						String tb = vals[1];
						if(!tb.equals(table))
						{   
							Tree.set_cell_offset(file, page, k, cell_addr[t]);
							k++;
						}
					}
					Tree.set_cell_number(file, page, (byte)k);
				}
				else
				continue;
			}
            
			file = new RandomAccessFile(dirCatalog+"davisbase_columns.tbl", "rw");
			num_of_pages = pages(file);
			for(int page = 1; page <= num_of_pages; page ++){
				file.seek((page-1)*page_size);
				byte file_type = file.readByte();
				if(file_type == 0x0D)
				{       short[] cell_addr = Tree.get_cell_array(file, page);
					int k = 0;
					for(int t = 0; t < cell_addr.length; t++)
					{       long position = Tree.get_cell_position(file, page, t);
						String[] vals = retrieve_values(file, position);
						String tb = vals[1];
						if(!tb.equals(table))
						{       Tree.set_cell_offset(file, page, k, cell_addr[t]);
							k++;
						}
					}
					Tree.set_cell_number(file, page, (byte)k);
				}
				else
					continue;
			}
                        File OFile = new File(dirUsrData, table+".tbl"); 
			OFile.delete();
		}catch(Exception e){
			System.out.println(e);
		}

	}
    
	public static int pages(RandomAccessFile file)
	{
		int num_pages = 0;
		try
		{
			num_pages = (int)(file.length()/(new Long(page_size)));
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
        return num_pages;
	}

	public static String[] retrieve_values(RandomAccessFile file, long position)
	{
		String[] val = null;
		try{    SimpleDateFormat dateFormat = new SimpleDateFormat (date_pattern);
                        file.seek(position+2);
			int key = file.readInt();
			int num_cols = file.readByte();
			byte[] st = new byte[num_cols];
			file.read(st);
			val = new String[num_cols+1];
			val[0] = Integer.toString(key);
			
			for(int t=1; t <= num_cols; t++){
				switch(st[t-1]){
					case 0x00:  file.readByte();
					            val[t] = "null";
								break;
                                        case 0x01:  file.readShort();
					            val[t] = "null";
								break;
                                        case 0x02:  file.readInt();
					            val[t] = "null";
								break;
                                        case 0x03:  file.readLong();
					            val[t] = "null";
								break;

					case 0x04:  val[t] = Integer.toString(file.readByte());
								break;
					case 0x05:  val[t] = Integer.toString(file.readShort());
								break;
					case 0x06:  val[t] = Integer.toString(file.readInt());
								break;
					case 0x07:  val[t] = Long.toString(file.readLong());
								break;
					case 0x08:  val[t] = String.valueOf(file.readFloat());
								break;
					case 0x09:  val[t] = String.valueOf(file.readDouble());
								break;
					case 0x0A:  Long temp = file.readLong();
								Date dateTime = new Date(temp);
								val[t] = dateFormat.format(dateTime);
								break;
					case 0x0B:  temp = file.readLong();
								Date date = new Date(temp);
								val[t] = dateFormat.format(date).substring(0,10);
								break;
					default:    int len = new Integer(st[t-1]-0x0C); //why 12 is subtracted?
								byte[] bytes = new byte[len];
								file.read(bytes);
								val[t] = new String(bytes);
								break;
				}
			}

		}catch(Exception e){
			System.out.println(e);
		}

		return val;
	}

	
	public static void insert_into(String table, String[] val,String dir_s)
	{
		try
		{
			RandomAccessFile file = new RandomAccessFile(dir_s+table+".tbl", "rw");
			insert_into(file, table, val);
			file.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	public static void create_table(String table, String[] col)
	{
		try
		{	
			RandomAccessFile file = new RandomAccessFile(dirUsrData+table+".tbl", "rw");
			file.setLength(page_size);
			file.seek(0);
			file.writeByte(0x0D);
			file.close();
			file = new RandomAccessFile(dirCatalog+"davisbase_tables.tbl", "rw");
			int num_of_pages = pages(file);
			int page=1;
			for(int p = 1; p <= num_of_pages; p++){
				int right_most = Tree.get_right_most(file, p);
				if(right_most == 0)
					page = p;
			}
			
			int[] keys = Tree.get_key_array(file, page);
			int k = keys[0];
			for(int t = 0; t < keys.length; t++)
				if(keys[t]>k)
					k = keys[t];
			file.close();
			String[] val = {Integer.toString(k+1), table};
			insert_into("davisbase_tables", val,dirCatalog);
                        file = new RandomAccessFile(dirCatalog+"davisbase_columns.tbl", "rw");
			num_of_pages = pages(file);
			page=1;
			for(int p = 1; p <= num_of_pages; p++){
				int right_most = Tree.get_right_most(file, p);
				if(right_most == 0)
					page = p;
			}
			
			keys = Tree.get_key_array(file, page);
			k = keys[0];
			for(int t = 0; t < keys.length; t++)
				if(keys[t]>k)
					k = keys[t];
			file.close();

			for(int t = 0; t < col.length; t++){
				k = k + 1;
				String[] token = col[t].split(" ");
				String col_name = token[0];
				String dt = token[1].toUpperCase();
				String pos = Integer.toString(t+1);
				String null_able;
				String uniq="NO";
				if(token.length > 2)
				{
					null_able = "NO";
					if(token[2].toUpperCase().trim().equals("UNIQUE"))
						uniq = "YES";
					else
						uniq = "NO";
				}
				else
					 null_able = "YES";
				String[] value = {Integer.toString(k), table, col_name, dt, pos, null_able, uniq};
				insert_into("davisbase_columns", value,dirCatalog);
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	
	public static void insert_into(RandomAccessFile file, String table, String[] val)
	{
		String[] dataType = get_data_type(table);
		String[] null_able = get_nullable(table);
		String[] uniq = get_unique(table);
		
		int num_of_pages = pages(file);
		int pages=1;
		for(int p = 1; p <= num_of_pages; p++){
			int right_most = Tree.get_right_most(file, p);
			if(right_most == 0)
				pages = p;
		}
		
		int[] keys = Tree.get_key_array(file, pages);
		int k = 0;
		for(int t = 0; t < keys.length; t++)
			if(keys[t]>k)
				k = keys[t];
		
		if (val[0].isEmpty())
			val[0]=String.valueOf(k+1);

		for(int t = 0; t < null_able.length; t++)
			if(val[t].equals("null") && null_able[t].equals("NO")){
				System.out.println("NULL-value constraint violation");
				System.out.println();
				return;
			}
		
		for(int t = 0; t < uniq.length; t++)
			if(uniq[t].equals("YES")){
				String path = dirUsrData ;
				
				try {
				
				RandomAccessFile uni = new RandomAccessFile(path+table+".tbl", "rw");
				String[] col_name = get_col_name(table);
				String[] type = get_data_type(table);
				Rows recs = new Rows();
				String[] cmp = {col_name[t],"=",val[t]};
				filter(uni, cmp, col_name, type, recs);
				if (recs.numRow>0)
				{       System.out.println("Duplicate key found for "+col_name[t].toString());
					System.out.println();
					return;
				}
				uni.close();
				System.out.println("Insertion Successfully. ");
				}catch (Exception e)
				{
					System.out.println(e);
				}
			}
                int key = new Integer(val[0]);
		int page = search_key_page(file, key);
		if(page != 0)
			if(Tree.has_key(file, page, key)){
				System.out.println("Uniqueness constraint violation");
				return;
			}
		if(page == 0)
			page = 1;
		
		byte[] st = new byte[dataType.length-1];
		short pl_size = (short) cal_payload_size(table, val, st);
		int cell_size = pl_size + 6;
		int off_set = Tree.check_leaf_space(file, page, cell_size);
		if(off_set != -1){
			Tree.insert_leaf_cell(file, page, off_set, pl_size, key, st, val);

		}else{
			Tree.split_leaf(file, page);
			insert_into(file, table, val);
		}
	}

	
	

	public static byte get_Stc(String value, String dataType)
	{
		if (dataType=="FLOAT")
		{
			dataType="DOUBLE";
		}
		if(value.equals("null"))
		{
			switch(dataType){
				case "DATE":        return 0x03;
				case "TEXT":        return 0x03;
				case "DOUBLE":      return 0x03;
				case "DATETIME":    return 0x03;
				case "TINYINT":     return 0x00;
				case "SMALLINT":    return 0x01;
				case "INT":         return 0x02;
				case "BIGINT":      return 0x03;
				case "REAL":        return 0x02;
				
                                default:            return 0x00;
			}							
		}else{
			switch(dataType){
				case "INT":         return 0x06;
				case "BIGINT":      return 0x07;
				case "TINYINT":     return 0x04;
				case "SMALLINT":    return 0x05;
				case "DATETIME":    return 0x0A;
				case "DATE":        return 0x0B;
				case "REAL":        return 0x08;
				case "DOUBLE":      return 0x09;
				case "TEXT":        return (byte)(value.length()+0x0C);
				default:            return 0x00;
			}
		}
	}
	
public static int cal_payload_size(String table, String[] vals, byte[] st){
		String[] dataType = get_data_type(table);
		int size =dataType.length;
		for(int t = 1; t < dataType.length; t++){
			st[t - 1]= get_Stc(vals[t], dataType[t]);
			size = size + feild_Length(st[t - 1]);
		}
		return size;
	}
    public static short feild_Length(byte st){
		switch(st){
			
			case 0x06: return 4;
			case 0x08: return 4;
			case 0x07: return 8;
			case 0x09: return 8;
			case 0x0A: return 8;
			case 0x00: return 1;
			case 0x0B: return 8;
                        
                        case 0x01: return 2;
			case 0x03: return 8;
			case 0x02: return 4;
			case 0x05: return 2;
			
                        case 0x04: return 1;
			default:   return (short)(st - 0x0C);
		}
	}


	
	
	public static String[] get_data_type(String table){
		String[] dataType = new String[0];
		try{
			RandomAccessFile file = new RandomAccessFile(dirCatalog+"davisbase_columns.tbl", "rw");
			Rows recs = new Rows();
			String[] col_name = {"row_id", "table_name", "column_name", "data_type", "ordinal_position", "is_nullable","is_unique"};
			String[] cmp = {"table_name","=",table};
			filter(file, cmp, col_name, recs);
			HashMap<Integer, String[]> content = recs.content;
			ArrayList<String> aList = new ArrayList<String>();
			for(String[] x : content.values()){
				aList.add(x[3]);
			}
			int size=aList.size();
			dataType = aList.toArray(new String[size]);
			file.close();
			return dataType;
		}catch(Exception e){
			System.out.println(e);
		}
		return dataType;
	}
public static int search_key_page(RandomAccessFile file, int key){
		int val2 = 1;
		try{
			int num_pages = pages(file);
			for(int page = 1; page <= num_pages; page++){
				file.seek((page - 1)*page_size);
				byte page_type = file.readByte();
				if(page_type == 0x0D){
					int[] keys = Tree.get_key_array(file, page);
					if(keys.length == 0)
						return 0;
					int right_most = Tree.get_right_most(file, page);
					if(keys[0] <= key && key <= keys[keys.length - 1]){
						return page;
					}else if(right_most == 0 && keys[keys.length - 1] < key){
						return page;
					}
				}
			}
		}catch(Exception e){
			System.out.println(e);
		}

		return val2;
	}



	public static String[] get_col_name(String table){
		String[] cols = new String[0];
		try{
			RandomAccessFile file = new RandomAccessFile(dirCatalog+"davisbase_columns.tbl", "rw");
			Rows recs = new Rows();
			String[] col_name = {"row_id", "table_name", "column_name", "data_type", "ordinal_position", "is_nullable","is_unique"};
			String[] cmp = {"table_name","=",table};
			filter(file, cmp, col_name, recs);
			HashMap<Integer, String[]> content = recs.content;
			ArrayList<String> aList = new ArrayList<String>();
			for(String[] t : content.values()){
				aList.add(t[2]);
			}
			int size=aList.size();
			cols = aList.toArray(new String[size]);
			file.close();
			return cols;
		}catch(Exception e){
			System.out.println(e);
		}
		return cols;
	}

	
	public static String[] get_unique(String table){
		String[] uniq = new String[0];
		try{
			RandomAccessFile file = new RandomAccessFile(dirCatalog+"davisbase_columns.tbl", "rw");
			Rows recs = new Rows();
			String[] col_name = {"row_id", "table_name", "column_name", "data_type", "ordinal_position", "is_nullable", "is_unique"};
			String[] cmp = {"table_name","=",table};
			filter(file, cmp, col_name, recs);
			HashMap<Integer, String[]> cont = recs.content;
			ArrayList<String> aList = new ArrayList<String>();
			for(String[] t : cont.values()){
				aList.add(t[6]);
			}
			int size=aList.size();
			uniq = aList.toArray(new String[size]);
			file.close();
			return uniq;
		}catch(Exception e){
			System.out.println(e);
		}
		return uniq;
	}
	public static String[] get_nullable(String table){
		String[] null_able = new String[0];
		try{
			RandomAccessFile file = new RandomAccessFile(dirCatalog+"davisbase_columns.tbl", "rw");
			Rows recs = new Rows();
			String[] col_name = {"row_id", "table_name", "column_name", "data_type", "ordinal_position", "is_nullable"};
			String[] cmp = {"table_name","=",table};
			filter(file, cmp, col_name, recs);
			HashMap<Integer, String[]> content = recs.content;
			ArrayList<String> aList = new ArrayList<String>();
			for(String[] t : content.values()){
				aList.add(t[5]);
			}
			int size=aList.size();
			null_able = aList.toArray(new String[size]);
			file.close();
			return null_able;
		}catch(Exception e){
			System.out.println(e);
		}
		return null_able;
	}

	public static void select(String table, String[] cols, String[] cmp,String dir_s){
		try{
			String path = dirUsrData ;
			if (table.equalsIgnoreCase("davisbase_tables") || table.equalsIgnoreCase("davisbase_columns"))
				path = dirCatalog ;
			
			RandomAccessFile file = new RandomAccessFile(path+table+".tbl", "rw");
			String[] col_name = get_col_name(table);
			String[] type = get_data_type(table);
			
			Rows recs = new Rows();
			
			if (cmp.length > 0 && cmp[1].equals("=") && cmp[2].equalsIgnoreCase("null")) 
			{
				System.out.println("Empty Set");
				return ;
			}
			
			if (cmp.length > 0 && cmp[1].equals("!=") && cmp[2].equalsIgnoreCase("null")) 
			{
				cmp = new String[0];
			}
			
			filter(file, cmp, col_name, type, recs);
			recs.display(cols); 
			file.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}


	public static void filter(RandomAccessFile file, String[] cmp, String[] col_name, Rows recs){
		try{    int num_of_pages = pages(file);
			for(int page = 1; page <= num_of_pages; page++){
				file.seek((page-1)*page_size);
				byte page_type = file.readByte();
				if(page_type == 0x0D)
				{       byte num_of_cells = Tree.get_cell_number(file, page);
                                        for(int t=0; t < num_of_cells; t++){
						
						long position = Tree.get_cell_position(file, page, t);	
						String[] vals = retrieve_values(file, position);
						int row_id=Integer.parseInt(vals[0]);

						boolean chck = cmp_Check(vals, row_id, cmp, col_name);
						
						if(chck)
							recs.add(row_id, vals);
					}
				}
				else
					continue;
			}

			recs.col_name = col_name;
			recs.format = new int[col_name.length];

		}catch(Exception e){
			System.out.println("Error at filter");
			e.printStackTrace();
		}

	}

	public static void filter(RandomAccessFile file, String[] cmp, String[] col_name, String[] type, Rows recs){
		try{
			
			int num_of_pages = pages(file);
			
			for(int page = 1; page <= num_of_pages; page++){
				
				file.seek((page-1)*page_size);
				byte page_type = file.readByte();
				
					if(page_type == 0x0D){
						
					byte num_of_cells = Tree.get_cell_number(file, page);

					 for(int t=0; t < num_of_cells; t++){
						long position = Tree.get_cell_position(file, page, t);
						String[] vals = retrieve_values(file, position);
						int row_id=Integer.parseInt(vals[0]);
						
						for(int y=0; y < type.length; y++)
							if(type[y].equals("DATE") || type[y].equals("DATETIME"))
								vals[y] = "'"+vals[y]+"'";
						
						boolean chck = cmp_Check(vals, row_id , cmp, col_name);

						
						for(int y=0; y < type.length; y++)
							if(type[y].equals("DATE") || type[y].equals("DATETIME"))
								vals[y] = vals[y].substring(1, vals[y].length()-1);

						if(chck)
							recs.add(row_id, vals);
					 }
				   }
				    else
						continue;
			}

			recs.col_name = col_name;
			recs.format = new int[col_name.length];

		}catch(Exception e){
			System.out.println("Error at filter");
			e.printStackTrace();
		}

	}



	
	public static boolean cmp_Check(String[] val, int row_id, String[] cmp, String[] col_name){

		boolean chck = false;
		
		if(cmp.length == 0){
			chck = true;
		}
		else{
			int col_pos = 1;
			for(int t = 0; t < col_name.length; t++){
				if(col_name[t].equals(cmp[0])){
					col_pos = t + 1;
					break;
				}
			}
			
			if(col_pos == 1){
				int val2 = Integer.parseInt(cmp[2]);
				String op = cmp[1];
				switch(op){
					case ">": if(row_id > val2) 
								chck = true;
							  else
							  	chck = false;
							  break;
					case "=": if(row_id == val2) 
								chck = true;
							  else
							  	chck = false;
							  break;
					
                                        case ">=": if(row_id >= val2) 
						        chck = true;
					          else
					  	        chck = false;	
					          break;
					case "<=": if(row_id <= val2) 
								chck = true;
							  else
							  	chck = false;	
							  break;
					case "<": if(row_id < val2) 
								chck = true;
							  else
							  	chck = false;
							  break;
					
                                        case "!=": if(row_id != val2)  
								chck = true;
							  else
							  	chck = false;	
							  break;						  							  							  							
				}
			}else{
				if(cmp[2].equals(val[col_pos-1]))
					chck = true;
				else
					chck = false;
			}
		}
		return chck;
	}
}




class Tree
{
	public static String dirCatalog = "data/catalog/";
	public static int page_size = 512;
	public static String dirUsrData = "data/user_data/";
        public static final String date_pattern = "yyyy-MM-dd_HH:mm:ss";
	
	
	
	public static int make_interior_page(RandomAccessFile file)
	{
		int num_pages = 0;
		try
		{
			num_pages = (int)(file.length()/(new Long(page_size)));
			num_pages = num_pages + 1;
			file.setLength(page_size * num_pages);
			file.seek((num_pages-1)*page_size);
			file.writeByte(0x05); 
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return num_pages;
	}
	public static short cal_payload_size(String[] val, String[] data_type)
	{
		int val2 = data_type.length; 
		for(int k = 1; k < data_type.length; k++)
		{
			String dType = data_type[k];
			switch(dType)
			{
				case "BIGINT":
					val2 = val2 + 8;
					break;
				case "REAL":
					val2 = val2 + 4;
					break;		
				case "TINYINT":
					val2 = val2 + 1;
					break;
				case "SMALLINT":
					val2 = val2 + 2;
					break;
				case "INT":
					val2 = val2 + 4;
					break;
				case "TEXT":
					String text = val[k];
					int len = text.length();
					val2 = val2 + len;
					break;
				case "DOUBLE":
					val2 = val2 + 8;
					break;
				case "DATE":
					val2 = val2 + 8;
					break;
                                case "DATETIME":
					val2 = val2 + 8;
					break;
				
                default: break;
			}
		}
		return (short)val2;
	}

	
	public static int find_mid_key(RandomAccessFile file, int page)
	{
		int val2 = 0;
		try
		{
			file.seek((page-1)*page_size);
			byte page_type = file.readByte();
			int num_cells = get_cell_number(file, page);
			int middle = (int) Math.ceil((double) num_cells / 2);
			long position = get_cell_position(file, page, middle-1);
			file.seek(position);

			switch(page_type)
			{
				case 0x0D:
					file.readShort();
					val2 = file.readInt();
					break;
                                case 0x05:
					file.readInt(); 
					val2 = file.readInt();
					break;
				        
			}

		}catch(Exception e)
		{
			System.out.println(e);
		}

		return val2;
	}

	public static int make_leaf_page(RandomAccessFile file){
		int num_pages = 0;
		try
		{
			num_pages = (int)(file.length()/(new Long(page_size)));
			num_pages = num_pages + 1;
			file.setLength(page_size * num_pages);
			file.seek((num_pages-1)*page_size);
			file.writeByte(0x0D); 
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return num_pages;

	}

	
	public static void splitInteriorPage(RandomAccessFile file, int curr_page, int new_page){
		try
		{
			
			int num_cells = get_cell_number(file, curr_page);
			
			int middle = (int) Math.ceil((double) num_cells / 2);

			int num_cell_A = middle - 1;
			int num_cell_B = num_cells - num_cell_A - 1;
			short content = 512;

			for(int k = num_cell_A+1; k < num_cells; k++)
			{
				long position = get_cell_position(file, curr_page, k);
				short cellSize = 8;
				content = (short)(content - cellSize);
				file.seek(position);
				byte[] cell = new byte[cellSize];
				file.read(cell);
				file.seek((new_page-1)*page_size+content);
				file.write(cell);
				file.seek(position);
				int page = file.readInt();
				set_parent(file, page, new_page);
				set_cell_offset(file, new_page, k - (num_cell_A + 1), content);
			}
			
			int tmp = get_right_most(file, curr_page);
			set_right_most(file, new_page, tmp);
			long midLoc = get_cell_position(file, curr_page, middle - 1);
			file.seek(midLoc);
			tmp = file.readInt();
			set_right_most(file, curr_page, tmp);
			file.seek((new_page-1)*page_size+2);
			file.writeShort(content);
			short off_set = getCellOffset(file, curr_page, num_cell_A-1);
			file.seek((curr_page-1)*page_size+2);
			file.writeShort(off_set);
                        int parnt = get_parent(file, curr_page);
			set_parent(file, new_page, parnt);
			byte number = (byte) num_cell_A;
			set_cell_number(file, curr_page, number);
			number = (byte) num_cell_B;
			set_cell_number(file, new_page, number);
			
		}catch(Exception e){
			System.out.println(e);
		}
	}
public static void split_leaf_page(RandomAccessFile file, int curr_page, int new_page){
		try{
			int num_cells = get_cell_number(file, curr_page);
			int middle = (int) Math.ceil((double) num_cells / 2);
                        int num_cell_A = middle - 1;
			int num_cell_B = num_cells - num_cell_A;
			int content = 512;

			for(int k = num_cell_A; k < num_cells; k++){
				long position = get_cell_position(file, curr_page, k);
				file.seek(position);
				int cellSize = file.readShort()+6;
				content = content - cellSize;
				file.seek(position);
				byte[] cell = new byte[cellSize];
				file.read(cell);
				file.seek((new_page-1)*page_size+content);
				file.write(cell);
				set_cell_offset(file, new_page, k - num_cell_A, content);
			}

			
			file.seek((new_page-1)*page_size+2);
			file.writeShort(content);
                        short off_set = getCellOffset(file, curr_page, num_cell_A-1);
			file.seek((curr_page-1)*page_size+2);
			file.writeShort(off_set);
                        int rightMost = get_right_most(file, curr_page);
			set_right_most(file, new_page, rightMost);
			set_right_most(file, curr_page, new_page);
                        int parnt = get_parent(file, curr_page);
			set_parent(file, new_page, parnt);
                        byte number = (byte) num_cell_A;
			set_cell_number(file, curr_page, number);
			number = (byte) num_cell_B;
			set_cell_number(file, new_page, number);
			
		}catch(Exception e){
			System.out.println(e);
			
		}
	}
	
	
	public static void split_leaf(RandomAccessFile file, int page){
		int new_page = make_leaf_page(file);
		int mid_key = find_mid_key(file, page);
		split_leaf_page(file, page, new_page);
		int parnt = get_parent(file, page);
		if(parnt == 0){
			int root_page = make_interior_page(file);
			set_parent(file, page, root_page);
			set_parent(file, new_page, root_page);
			set_right_most(file, root_page, new_page);
			insert_interior_cell(file, root_page, page, mid_key);
		}else{
			long pointer_position = get_pointer_loc(file, page, parnt);
			set_pointer_loc(file, pointer_position, parnt, new_page);
			insert_interior_cell(file, parnt, page, mid_key);
			sort_cell_array(file, parnt);
			while(check_interior_space(file, parnt)){
				parnt = splitInterior(file, parnt);
			}
		}
	}

	public static int splitInterior(RandomAccessFile file, int page){
		int new_page = make_interior_page(file);
		int mid_key = find_mid_key(file, page);
		splitInteriorPage(file, page, new_page);
		int parnt = get_parent(file, page);
		if(parnt == 0){
			int root_page = make_interior_page(file);
			set_parent(file, page, root_page);
			set_parent(file, new_page, root_page);
			set_right_most(file, root_page, new_page);
			insert_interior_cell(file, root_page, page, mid_key);
			return root_page;
		}else{
			long pointer_position = get_pointer_loc(file, page, parnt);
			set_pointer_loc(file, pointer_position, parnt, new_page);
			insert_interior_cell(file, parnt, page, mid_key);
			sort_cell_array(file, parnt);
			return parnt;
		}
	}

	
	public static void sort_cell_array(RandomAccessFile file, int page){
		 byte number = get_cell_number(file, page);
		 int[] keyArray = get_key_array(file, page);
		 short[] cellArray = get_cell_array(file, page);
		 int ltmp;
		 short rtmp;

		 for (int k = 1; k < number; k++) {
                    for(int y = k ; y > 0 ; y--){
                        if(keyArray[y] < keyArray[y-1]){
                            ltmp = keyArray[y];
                            keyArray[y] = keyArray[y-1];
                            keyArray[y-1] = ltmp;
                            rtmp = cellArray[y];
                            cellArray[y] = cellArray[y-1];
                            cellArray[y-1] = rtmp;
                        }
                    }
                 }

         try{
         	file.seek((page-1)*page_size+12);
         	for(int k = 0; k < number; k++){
				file.writeShort(cellArray[k]);
			}
         }catch(Exception e){
         	System.out.println("Error at sort_cell_array");
         }
	}

	public static short[] get_cell_array(RandomAccessFile file, int page){
		int number = new Integer(get_cell_number(file, page));
		short[] list = new short[number];

		try{
			file.seek((page-1)*page_size+12);
			for(int k = 0; k < number; k++){
				list[k] = file.readShort();
			}
		}catch(Exception e){
			System.out.println(e);
		}

		return list;
	}

	public static int[] get_key_array(RandomAccessFile file, int page){
		int number = new Integer(get_cell_number(file, page));
		int[] list = new int[number];

		try{
			file.seek((page-1)*page_size);
			byte page_type = file.readByte();
			byte off_set = 0;
			switch(page_type){
			    	case 0x05:
					off_set = 4;
					break;
                                case 0x0d:
                                        off_set = 2;
                                        break;

                                default:
					off_set = 2;
					break;
			}

			for(int k = 0; k < number; k++){
				long position = get_cell_position(file, page, k);
				file.seek(position+off_set);
				list[k] = file.readInt();
			}

		}catch(Exception e){
			System.out.println(e);
		}

		return list;
	}
	
	
	public static void set_pointer_loc(RandomAccessFile file, long position, int parnt, int page){
		try{
			if(position == 0){
				file.seek((parnt-1)*page_size+4);
			}else{
				file.seek(position);
			}
			file.writeInt(page);
		}catch(Exception e){
			System.out.println(e);
		}
	} 

	public static long get_pointer_loc(RandomAccessFile file, int page, int parnt){
		long val2 = 0;
		try{
			int num_cells = new Integer(get_cell_number(file, parnt));
			for(int k=0; k < num_cells; k++){
				long position = get_cell_position(file, parnt, k);
				file.seek(position);
				int child_page = file.readInt();
				if(child_page == page){
					val2 = position;
				}
			}
		}catch(Exception e){
			System.out.println(e);
		}

		return val2;
	}

	
	public static void insert_leaf_cell(RandomAccessFile file, int page, int off_set, short pl_size, int key, byte[] st, String[] val){
		try{
			String ss;
			file.seek((page-1)*page_size+off_set);
			file.writeShort(pl_size);
			file.writeInt(key);
			int column = val.length - 1;
			file.writeByte(column);
			file.write(st);
			for(int k = 1; k < val.length; k++){
				switch(st[k-1]){
					case 0x0B:
						ss = val[k];
						ss = ss.substring(1, ss.length()-1);
						ss = ss+"_00:00:00";
						Date tmp2 = new SimpleDateFormat(date_pattern).parse(ss);
						long time2 = tmp2.getTime();
						file.writeLong(time2);
						break;
					case 0x0A:
						ss = val[k];
						Date tmp = new SimpleDateFormat(date_pattern).parse(ss.substring(1, ss.length()-1));
						long time = tmp.getTime();
						file.writeLong(time);
						break;
					case 0x09:
						file.writeDouble(new Double(val[k]));
						break;
					case 0x08:
						file.writeFloat(new Float(val[k]));
						break;
					case 0x07:
						file.writeLong(new Long(val[k]));
						break;
					case 0x06:
						file.writeInt(new Integer(val[k]));
						break;
					case 0x05:
						file.writeShort(new Short(val[k]));
						break;
					case 0x04:
						file.writeByte(new Byte(val[k]));
						break;
					case 0x03:
						file.writeLong(0);
						break;
					case 0x02:
						file.writeInt(0);
						break;
					case 0x01:
						file.writeShort(0);
						break;
					case 0x00:
						file.writeByte(0);
						break;
					
                                        default:
						file.writeBytes(val[k]);
						break;
				}
			}
			int n = get_cell_number(file, page);
			byte tmp = (byte) (n+1);
			set_cell_number(file, page, tmp);
			file.seek((page-1)*page_size+12+n*2);
			file.writeShort(off_set);
			file.seek((page-1)*page_size+2);
			int content = file.readShort();
			if(content >= off_set || content == 0){
				file.seek((page-1)*page_size+2);
				file.writeShort(off_set);
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}
	public static void insert_interior_cell(RandomAccessFile file, int page, int children, int key){
		try{
			
			file.seek((page-1)*page_size+2);
			short content = file.readShort();
			if(content == 0)
				content = 512;
			
			content = (short)(content - 8);
			file.seek((page-1)*page_size+content);
			file.writeInt(children);
			file.writeInt(key);
			file.seek((page-1)*page_size+2);
			file.writeShort(content);
			byte number = get_cell_number(file, page);
			set_cell_offset(file, page ,number, content);
			number = (byte) (number + 1);
			set_cell_number(file, page, number);

		}catch(Exception e){
			System.out.println(e);
		}
	}


	
	public static boolean check_interior_space(RandomAccessFile file, int page){
		byte num_cells = get_cell_number(file, page);
		if(num_cells > 30)
			return true;
		else
			return false;
	}
	public static void updateLeafCell(RandomAccessFile file, int page, int off_set, int pl_size, int key, byte[] st, String[] val){
		try{
			String ss;
			file.seek((page-1)*page_size+off_set);
			file.writeShort(pl_size);
			file.writeInt(key);
			int column = val.length - 1;
			file.writeByte(column);
			file.write(st);
			for(int k = 1; k < val.length; k++){
				switch(st[k-1]){
					case 0x00:
						file.writeByte(0);
						break;
					case 0x01:
						file.writeShort(0);
						break;
					case 0x02:
						file.writeInt(0);
						break;
					case 0x03:
						file.writeLong(0);
						break;
					case 0x04:
						file.writeByte(new Byte(val[k]));
						break;
					case 0x05:
						file.writeShort(new Short(val[k]));
						break;
					case 0x06:
						file.writeInt(new Integer(val[k]));
						break;
					case 0x07:
						file.writeLong(new Long(val[k]));
						break;
					case 0x08:
						file.writeFloat(new Float(val[k]));
						break;
					case 0x09:
						file.writeDouble(new Double(val[k]));
						break;
					case 0x0A:
						ss = val[k];
						Date tmp = new SimpleDateFormat(date_pattern).parse(ss.substring(1, ss.length()-1));
						long time = tmp.getTime();
						file.writeLong(time);
						break;
					case 0x0B:
						ss = val[k];
						ss = ss.substring(1, ss.length()-1);
						ss = ss+"_00:00:00";
						Date tmp2 = new SimpleDateFormat(date_pattern).parse(ss);
						long time2 = tmp2.getTime();
						file.writeLong(time2);
						break;
					default:
						file.writeBytes(val[k]);
						break;
				}
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}


	
	public static int get_parent(RandomAccessFile file, int page){
		int val2 = 0;

		try{
			file.seek((page-1)*page_size+8);
			val2 = file.readInt();
		}catch(Exception e){
			System.out.println(e);
		}

		return val2;
	}
	public static int check_leaf_space(RandomAccessFile file, int page, int size){
		int val2 = -1;

		try{
			file.seek((page-1)*page_size+2);
			int content = file.readShort();
			if(content == 0)
				return page_size - size;
			int num_cells = get_cell_number(file, page);
			int space = content - 20 - 2*num_cells;
			if(size < space)
				return content - size;
			
		}catch(Exception e){
			System.out.println(e);
		}

		return val2;
	}


	public static int get_right_most(RandomAccessFile file, int page){
		int rm = 0;

		try{
			file.seek((page-1)*page_size+4);
			rm = file.readInt();
		}catch(Exception e){
			System.out.println("Failure at get_right_most");
		}

		return rm;
	}
	public static void set_parent(RandomAccessFile file, int page, int parnt){
		try{
			file.seek((page-1)*page_size+8);
			file.writeInt(parnt);
		}catch(Exception e){
			System.out.println(e);
		}
	}
	

	public static void set_right_most(RandomAccessFile file, int page, int right_leaf){

		try{
			file.seek((page-1)*page_size+4);
			file.writeInt(right_leaf);
		}catch(Exception e){
			System.out.println("Failure at set_right_most");
		}

	}

	public static long get_cell_position(RandomAccessFile file, int page, int id){
		long position = 0;
		try{
			file.seek((page-1)*page_size+12+id*2);
			short off_set = file.readShort();
			long orign = (page-1)*page_size;
			position = orign + off_set;
		}catch(Exception e){
			System.out.println(e);
		}
		return position;
	}
	public static boolean has_key(RandomAccessFile file, int page, int key){
		int[] keys = get_key_array(file, page);
		for(int k : keys)
			if(key == k)
				return true;
		return false;
	}
	


	public static void set_cell_number(RandomAccessFile file, int page, byte number){
		try{
			file.seek((page-1)*page_size+1);
			file.writeByte(number);
		}catch(Exception e){
			System.out.println(e);
		}
	}
	public static byte get_cell_number(RandomAccessFile file, int page){
		byte val2 = 0;

		try{
			file.seek((page-1)*page_size+1);
			val2 = file.readByte();
		}catch(Exception e){
			System.out.println(e);
		}

		return val2;
	}
	public static byte getPageType(RandomAccessFile file, int page){
		byte type=0x05;
		try {
			file.seek((page-1)*page_size);
			type = file.readByte();
		} catch (Exception e) {
			System.out.println(e);
		}
		return type;
	}
	
	public static void set_cell_offset(RandomAccessFile file, int page, int id, int off_set){
		try
		{
			file.seek((page-1)*page_size+12+id*2);
			file.writeShort(off_set);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	public static short getCellOffset(RandomAccessFile file, int page, int id)
	{
		short off_set = 0;
		try
		{
			file.seek((page-1)*page_size+12+id*2);
			off_set = file.readShort();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return off_set;
	}

    

}



class Rows
{
	public HashMap<Integer, String[]> content;
	public int numRow; 
	public int[] format; 
	public String[] col_name; 
	
	public void display(String[] col)
	{
		if(numRow == 0){
			System.out.println("Table is empty");
		}
		else
		{
			update_format();
			if(col[0].equals("*"))
			{
				for(int l: format)
					System.out.print(DavisBase.line("-", l+3));
                    System.out.println();
				
				for(int t = 0; t< col_name.length; t++)
					System.out.print(fix(format[t], col_name[t])+"|");
				
				System.out.println();
				
				for(int l: format)
					System.out.print(DavisBase.line("-", l+3));
				
				System.out.println();
                                for(String[] t : content.values()){
					for(int k = 0; k < t.length; k++)
						System.out.print(fix(format[k], t[k])+"|");
					System.out.println();
				}
			
			}
			else
			{
				int[] ctrl = new int[col.length];
				for(int k = 0; k < col.length; k++)
					for(int t = 0; t < col_name.length; t++)
						if(col[k].equals(col_name[t]))
							ctrl[k] = t;

				for(int k = 0; k < ctrl.length; k++)
					System.out.print(DavisBase.line("-", format[ctrl[k]]+3));
				
				System.out.println();
				for(int k = 0; k < ctrl.length; k++)
					System.out.print(fix(format[ctrl[k]], col_name[ctrl[k]])+"|");
				
				System.out.println();
				for(int k = 0; k < ctrl.length; k++)
					System.out.print(DavisBase.line("-", format[ctrl[k]]+3));
				
				System.out.println();
				for(String[] t : content.values())
				{
					for(int k = 0; k < ctrl.length; k++)
						System.out.print(fix(format[ctrl[k]], t[ctrl[k]])+"|");
					System.out.println();
				}
				System.out.println();
			}
		}
	}
	public void add(int row_id, String[] value)
	{
		content.put(row_id, value);
		numRow = numRow + 1;
	}
	public Rows()
	{
		numRow = 0;
		content = new HashMap<Integer, String[]>();
	}

	
	public String fix(int length, String s) 
	{
		return String.format("%-"+(length+3)+"s", s);
	}

	public void update_format()
	{
		for(int t = 0; t < format.length; t++)
			format[t] = col_name[t].length();
		for(String[] t : content.values())
			for(int k = 0; k < t.length; k++)
				if(format[k] < t[k].length())
					format[k] = t[k].length();
	}

	
}





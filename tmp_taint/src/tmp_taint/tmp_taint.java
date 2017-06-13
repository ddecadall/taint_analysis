package tmp_taint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import soot.PackManager;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.source.DefaultSourceSinkManager;
import soot.options.Options;

public class tmp_taint {
	public final static String jarPath="E:\\androiddev\\android-sdk_r24.3.4-windows\\android-sdk-windows\\platforms\\android-21\\android.jar";
	public final static String apkPath="E:\\soot\\file\\tmp_taint.apk";
	public final static String sourcePath="E:\\soot\\file\\source_taint.txt";
	public final static String sinkPath="E:\\soot\\file\\sink_taint.txt";
	public final static String out_file_path="E:\\soot\\file\\output_taint.txt";
	public final static String entrypointPath="E:\\soot\\file\\entrypoint_taint.txt";
	static List<String> sources,sinks,entrypoints;
	static Set<AndroidMethod> source_method,sink_method;
	static OutputStreamWriter writer;
	static BufferedReader sink_reader,source_reader,entrypoint_reader;
	static DefaultSourceSinkManager  ssmanager;
	static Infoflow info;
	private static SetupApplication target_app;
	private static SootMethod constructed_entry;
	
	
	public static void main(String args[]){
		set_options();
		
		runinfoflow();
	}
	static void set_options(){
		try {
			writer = new OutputStreamWriter(new FileOutputStream(new File(out_file_path)));
			sink_reader = new BufferedReader(new InputStreamReader(new FileInputStream(sinkPath)));
			source_reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourcePath)));
			//entrypoint_reader = new BufferedReader(new InputStreamReader(new FileInputStream(entrypointPath)));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	
		soot.G.reset();
		//target_app.calculateSourcesSinksEntrypoints(filePath);
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_process_dir(Collections.singletonList(apkPath));
		Options.v().set_force_android_jar(jarPath);
		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_whole_program(true);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_process_multiple_dex(true);
		Options.v().set_app(true);
		Scene.v().loadNecessaryClasses();
		try {
			get_sources_and_sinks();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		target_app = new SetupApplication(jarPath,apkPath);
		target_app_set();
		//constructed_entry = target_app.getEntryPointCreator().createDummyMain();
		
		//Options.v().set_main_class(constructed_entry.getSignature());
	//	Scene.v().setEntryPoints(Collections.singletonList(constructed_entry));
	//	PackManager.v().runPacks();
	}
	static void target_app_set(){
		//target_app.setSootConfig(config);
	}
	static void get_sources_and_sinks() throws IOException{
		String tmp_line;
		source_method = new HashSet<AndroidMethod>();
		sink_method = new HashSet<AndroidMethod>();
		sources = new ArrayList();
		sinks = new ArrayList();
		SootMethod tmp_method;
		while(true){
			tmp_line = source_reader.readLine();
			if(tmp_line==null){
				break;
			}
			else{
				sources.add(tmp_line);
			}
		}
		while(true){
			tmp_line = sink_reader.readLine();
			if(tmp_line==null){
				break;
			}
			else{
				sinks.add(tmp_line);
			}
		}
		int tmp_i;
		for(tmp_i=0;tmp_i<sources.size();tmp_i++){
			tmp_method = Scene.v().getMethod(sources.get(tmp_i));
			source_method.add(new AndroidMethod(tmp_method));
		}
		for(tmp_i=0;tmp_i<sinks.size();tmp_i++){
			tmp_method = Scene.v().getMethod(sinks.get(tmp_i));
			sink_method.add(new AndroidMethod(tmp_method));
		}
		
	}
	static void runinfoflow(){
		//target_app.setSootConfig(new SootConfigForAndroid());
		System.out.println("source method size : "+sink_method.size());
		try {
			target_app.calculateSourcesSinksEntrypoints(source_method, sink_method);
		} catch (IOException | XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InfoflowResults info_res = target_app.runInfoflow();
		info_res.printResults();
		System.out.println("work end");
	}
	static void calculate_entrypoints(){
		String tmp_line=null;
		while(true){
			try {
				tmp_line = entrypoint_reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(tmp_line==null){
				break;
			}
			else{
				entrypoints.add(tmp_line);
			}
		}
	}
}

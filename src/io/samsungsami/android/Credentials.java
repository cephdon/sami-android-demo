package io.samsungsami.android;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Credentials {
	
	private String token;
	private String id;
	private String name;
	private String email;
	private String password;
	
	public Credentials(){}
	
	public Credentials(String token, String id, String name, String email, String password){
		this.token = token;
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
	}
	
	public String getToken(){
		return this.token;
	}
	
	public String getId(){
		return this.id;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getEmail(){
		return this.email;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public void setToken(String token){
		this.token = token;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	/**
	 * Returns a credentials object from a JSON string
	 * @return
	 */
	public static Credentials fromJson(String json){
		Credentials credentials = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			credentials = mapper.readValue(json, Credentials.class);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return credentials;
	}
	
	/**
	 * Returns a JSON string from a credentials object
	 * @return
	 */
	public static String toJson(Credentials credentials){
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(credentials);
		} catch (JsonGenerationException ex) {
			ex.printStackTrace();
		} catch (JsonMappingException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return json;
	}
	
	  @Override
	  public String toString()  {
	    StringBuilder sb = new StringBuilder();
	    sb.append("class User {\n");
	    
	    sb.append("  token: ").append(token).append("\n");
	    sb.append("  id: ").append(id).append("\n");
	    sb.append("  email: ").append(name).append("\n");
	    sb.append("  name: ").append(email).append("\n");
	    sb.append("  password: ").append(password).append("\n");
	    sb.append("}\n");
	    return sb.toString();
	  }
}

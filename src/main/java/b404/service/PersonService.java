package main.java.b404.service;

import javax.crypto.SecretKey;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.IllegalFormatException;

// https://github.com/jwtk/jjwt
// https://dennis-xlc.gitbooks.io/restful-java-with-jax-rs-2-0-2rd-edition/en/part1/chapter15/securing_jax_rs.html

@Path("person")
public class PersonService {

}
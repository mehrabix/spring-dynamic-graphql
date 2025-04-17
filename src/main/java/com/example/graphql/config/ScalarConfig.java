package com.example.graphql.config;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class ScalarConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(GraphQLScalarType.newScalar()
                        .name("ID")
                        .description("Built-in ID")
                        .coercing(new Coercing<Object, String>() {
                            @Override
                            public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                                if (dataFetcherResult == null) {
                                    return null;
                                }
                                return dataFetcherResult.toString();
                            }

                            @Override
                            public Object parseValue(Object input) throws CoercingParseValueException {
                                return input.toString();
                            }

                            @Override
                            public Object parseLiteral(Object input) throws CoercingParseLiteralException {
                                if (input instanceof StringValue) {
                                    return ((StringValue) input).getValue();
                                }
                                return input.toString();
                            }
                        })
                        .build());
    }
} 
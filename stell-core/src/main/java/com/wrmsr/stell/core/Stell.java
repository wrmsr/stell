/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wrmsr.stell.core;

import com.wrmsr.stell.core.parse.JsonBaseVisitor;
import com.wrmsr.stell.core.parse.JsonLexer;
import com.wrmsr.stell.core.parse.JsonParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNull;

public class Stell
{
    public interface Node
    {
    }

    public interface Val extends Node
    {
    }

    public interface Prim extends Val
    {
    }

    public record True() implements Prim
    {
    }

    public record False() implements Prim
    {
    }

    public record Null() implements Prim
    {
    }

    public record Str(String s) implements Prim
    {
    }

    public record Num(String s) implements Prim
    {
    }

    public record Obj(List<Pair> pairs) implements Val
    {
    }

    public record Pair(String key, Val val) implements Node
    {
    }

    public record Arr(List<Val> vals) implements Val
    {
    }

    public static void sayHi()
    {
        String str = """
                {
                    "quiz": {
                        "sport": {
                            "q1": {
                                "question": "Which one is correct team name in NBA?",
                                "options": [
                                    "New York Bulls",
                                    null,
                                    "Golden State Warriros",
                                    "Huston Rocket"
                                ],
                                "answer": "Huston Rocket"
                            }
                        },
                        "maths": {
                            "q1": {
                                "question": "5 + 7 = ?",
                                "options": [
                                    "10",
                                    false,
                                    "12",
                                    true
                                ],
                                "answer": "12"
                            },
                            "q2": {
                                "question": "12 - 8 = ?",
                                "options": [
                                    1,
                                    2,
                                    3,
                                    4
                                ],
                                "answer": "4"
                            }
                        }
                    },
                    "answers": null
                }
                """;
        CharStream input = new CaseInsensitiveCharStream(CharStreams.fromString(str));
        JsonLexer lexer = new JsonLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JsonParser parser = new JsonParser(tokens);

        Val v = (Val) parser.json().accept(new JsonBaseVisitor<Node>()
        {
            @Override
            public Node visitJson(JsonParser.JsonContext ctx)
            {
                return visit(ctx.val());
            }

            @Override
            public Node visitObj(JsonParser.ObjContext ctx)
            {
                return new Obj(ctx.pair().stream().map(p -> (Pair) visit(p)).collect(toImmutableList()));
            }

            @Override
            public Node visitPair(JsonParser.PairContext ctx)
            {
                return new Pair(ctx.STR().getText(), (Val) visit(ctx.val()));
            }

            @Override
            public Node visitArr(JsonParser.ArrContext ctx)
            {
                return new Arr(ctx.val().stream().map(v -> (Val) visit(v)).collect(toImmutableList()));
            }

            @Override
            public Node visitPrim(JsonParser.PrimContext ctx)
            {
                if (ctx.STR() != null) {
                    return new Str(ctx.STR().getText());
                }
                if (ctx.NUM() != null) {
                    return new Num(ctx.NUM().getText());
                }
                if (ctx.TRUE() != null) {
                    return new True();
                }
                if (ctx.FALSE() != null) {
                    return new False();
                }
                if (ctx.NULL() != null) {
                    return new Null();
                }
                throw new IllegalStateException("bad primitive");
            }

            @Override
            protected Node aggregateResult(Node aggregate, Node nextResult)
            {
                checkState(aggregate == null);
                return requireNonNull(nextResult);
            }
        });

        System.out.println(v);
    }
}

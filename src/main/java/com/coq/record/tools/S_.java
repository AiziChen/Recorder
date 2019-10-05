package com.coq.record.tools;

import com.coq.record.type.Cons;

/**
 * List tools
 *
 * @author Quanyec
 * This source code is license on the Apache-License v2.0
 */
public class S_ {

    public static Cons car(String lsp) {
        int llen = lsp.length();
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < llen; ++i) {
            char c = lsp.charAt(i);
            if (c == '(') {
                // clear the left spaces
                while (i + 1 < llen && lsp.charAt(i + 1) == ' ') {
                    i++;
                }
                int leftB = 0;
                int rightB = 0;
                // if lsp[i] is '(', is a block
                if (i + 1 < llen && lsp.charAt(i + 1) == '(') {
                    // find from '(' to ')' content
                    while (i + 1 < llen) {
                        sb.append(lsp.charAt(i + 1));
                        if (lsp.charAt(i + 1) == '(') {
                            leftB++;
                        } else if (lsp.charAt(i + 1) == ')') {
                            rightB++;
                        }
                        i++;
                        if (rightB >= leftB) {
                            break;
                        }
                    }
                    break;
                }
                // if lsp[i] is a quote
                else if (i + 2 < llen && lsp.charAt(i + 2) == '(' && lsp.charAt(i + 1) == '\'') {
                    // find from '(' to ')' content
                    while (i + 2 < llen) {
                        sb.append(lsp.charAt(i + 1));
                        i++;
                        if (lsp.charAt(i + 1) == '(') {
                            leftB++;
                        } else if (lsp.charAt(i + 1) == ')') {
                            rightB++;
                        }
                        if (rightB >= leftB) {
                            break;
                        }
                    }
                    sb.append(lsp.charAt(i + 1));
                    break;
                } else if (i + 1 < llen && lsp.charAt(i + 1) == '"') {
                    while (i + 2 < llen && lsp.charAt(i + 2) != '"') {
                        i++;
                        sb.append(lsp.charAt(i));
                    }
                    sb.append(lsp.charAt(i + 1)).append('"');
                    break;
                }
                // if lsp[i] is a letter
                else if (i + 1 < llen && lsp.charAt(i + 1) != ')' && lsp.charAt(i + 1) != ' ') {
                    while (i + 1 < llen && lsp.charAt(i + 1) != '(' && lsp.charAt(i + 1) != ')' && lsp.charAt(i + 1) != ' ') {
                        i++;
                        sb.append(lsp.charAt(i));
                    }
                    break;
                }
            }
        }
        return new Cons(sb.toString(), null);
    }


    public static Cons cdr(String lsp) {
        int llen = lsp.length();
        String cars = car(lsp).getCarValue();
        int clen = cars.length();
        int i, j;
        // find the car-end point
        int ep = 1;
        int count;
        for (i = 0; i < llen; ++i) {
            count = 0;
            for (j = 0; j < clen; ++j) {
                if (lsp.charAt(i + j) == cars.charAt(j)) {
                    count++;
                    if (count >= clen) {
                        ep = i + clen;
                        break;
                    }
                }
            }
            if (count >= clen) {
                ep = i + clen;
                break;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (i = ep; i < llen; ++i) {
            sb.append(lsp.charAt(i));
        }

        return new Cons(null, sb.toString());
    }
}

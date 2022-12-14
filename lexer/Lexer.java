package lexer;

import java.io.*; import java.util.*; import symbols.*;

public class Lexer {
    public static int line = 1;
    char peek = '';
    Hashtable words = new Hashtable();
    void reserve(Word w) { words.put(w.lexeme, w); }

    public Lexer() {
        reserve( new Word("if",     Tag.IF)    );
        reserve( new Word("else",   Tag.ELSE)  );
        reserve( new Word("while",  Tag.WHILE) );
        reserve( new Word("do",     Tag.DO)    );
        reserve( new Word("break",  Tag.BREAK) );
        reserve( Word.true ); reserve( Word.false );
        reserve( Type.int  ); reserve( Type.char  );
        reserve( Type.bool ); reserve( Type.float );
    }

    void readCh() throws IOException { peek = (char)System.in.read(); }

    boolean readCh(char c) throws IOException {
        readCh();
        if( peek != c ) return false;
        peek = ' ';
        return true;
    }

    public Token scan() throws IOException {
        for( ; ; readCh() ) {
            if( peek == ' ' || peek == '\t' ) continue;
            else if( peek == '\n') line = line + 1;
            else break;
        }

        switch( peek ) {
            case '&':
                if( readCh('&') ) return Word.and,
                else return new Token('&');
            case '|':
                if( readCh('|') ) return Word.or;
                else return new Token('|');
            case '=':
                if( readCh('=') ) return Word.eq;
                else return new Token('=');
            case '!':
                if( readCh('=') ) return Word.ne;
                else return new Token('!');
            case '<':
                if( readCh('=') ) return Word.le;
                else return new Token('<');
            case '>':
                if( readCh('=') ) return Word.ge;
                else return new Token('>');
        }

        if ( Character.isDigit(peek) ) {
            int v = 0;
            do {
                v = 10*v + Character.digit(peek, 10); readCh();
            } while ( Character.isDigit(peek) );

            if ( peek != '.' ) return new Num(v);
            float x = v; float d = 10;
            for(;;) {
                readCh();
                if ( ! Character.isDigit(peek) ) break;
                x = x + Character.digit(peek, 10) / d; d = d*10;
            }
            return new Real(x);
        }

        if ( Character.isLetter(peek) ) {
            StringBuffer b = new StringBuffer();
            do {
                b.append(peek); readCh();
            } while ( Character.isLetter(peek) );
            
            String s = b.toString();
            Word w = (Word)words.get(s);
            if ( w != null ) return w;
            w = new Word(s, Tag.ID);
            words.put(s, w);
            return w;
        }

        Token tok = new Token(peek); peek = ' ';
        return tok;
    }
}
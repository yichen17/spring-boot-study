/**
 * This class is generated by jOOQ
 */
package sample.jooq.domain;

import javax.annotation.Generated;

/**
 * Convenience access to all tables in PUBLIC
 */
@Generated(value = { "http://www.jooq.org",
		"jOOQ version:3.8.2" }, comments = "This class is generated by jOOQ")
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

	/**
	 * The table <code>PUBLIC.LANGUAGE</code>.
	 */
	public static final Language LANGUAGE = sample.jooq.domain.Language.LANGUAGE;

	/**
	 * The table <code>PUBLIC.AUTHOR</code>.
	 */
	public static final Author AUTHOR = sample.jooq.domain.Author.AUTHOR;

	/**
	 * The table <code>PUBLIC.BOOK</code>.
	 */
	public static final Book BOOK = sample.jooq.domain.Book.BOOK;

	/**
	 * The table <code>PUBLIC.BOOK_STORE</code>.
	 */
	public static final BookStore BOOK_STORE = sample.jooq.domain.BookStore.BOOK_STORE;

	/**
	 * The table <code>PUBLIC.BOOK_TO_BOOK_STORE</code>.
	 */
	public static final BookToBookStore BOOK_TO_BOOK_STORE = sample.jooq.domain.BookToBookStore.BOOK_TO_BOOK_STORE;

}

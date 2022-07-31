/**
 * This class is generated by jOOQ
 */
package sample.jooq.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

/**
 * This class is generated by jOOQ.
 */
@Generated(value = { "http://www.jooq.org",
		"jOOQ version:3.8.2" }, comments = "This class is generated by jOOQ")
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends SchemaImpl {

	private static final long serialVersionUID = 217498103;

	/**
	 * The reference instance of <code>PUBLIC</code>
	 */
	public static final Public PUBLIC = new Public();

	/**
	 * The table <code>PUBLIC.LANGUAGE</code>.
	 */
	public final Language LANGUAGE = sample.jooq.domain.Language.LANGUAGE;

	/**
	 * The table <code>PUBLIC.AUTHOR</code>.
	 */
	public final Author AUTHOR = sample.jooq.domain.Author.AUTHOR;

	/**
	 * The table <code>PUBLIC.BOOK</code>.
	 */
	public final Book BOOK = sample.jooq.domain.Book.BOOK;

	/**
	 * The table <code>PUBLIC.BOOK_STORE</code>.
	 */
	public final BookStore BOOK_STORE = sample.jooq.domain.BookStore.BOOK_STORE;

	/**
	 * The table <code>PUBLIC.BOOK_TO_BOOK_STORE</code>.
	 */
	public final BookToBookStore BOOK_TO_BOOK_STORE = sample.jooq.domain.BookToBookStore.BOOK_TO_BOOK_STORE;

	/**
	 * No further instances allowed
	 */
	private Public() {
		super("PUBLIC", null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Catalog getCatalog() {
		return DefaultCatalog.DEFAULT_CATALOG;
	}

	@Override
	public final List<Table<?>> getTables() {
		List result = new ArrayList();
		result.addAll(getTables0());
		return result;
	}

	private final List<Table<?>> getTables0() {
		return Arrays.<Table<?>>asList(Language.LANGUAGE, Author.AUTHOR, Book.BOOK,
				BookStore.BOOK_STORE, BookToBookStore.BOOK_TO_BOOK_STORE);
	}

}

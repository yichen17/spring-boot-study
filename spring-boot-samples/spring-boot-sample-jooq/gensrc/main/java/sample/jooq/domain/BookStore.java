/**
 * This class is generated by jOOQ
 */
package sample.jooq.domain;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;

/**
 * This class is generated by jOOQ.
 */
@Generated(value = { "http://www.jooq.org",
		"jOOQ version:3.8.2" }, comments = "This class is generated by jOOQ")
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BookStore extends TableImpl<Record> {

	private static final long serialVersionUID = -2132596210;

	/**
	 * The reference instance of <code>PUBLIC.BOOK_STORE</code>
	 */
	public static final BookStore BOOK_STORE = new BookStore();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<Record> getRecordType() {
		return Record.class;
	}

	/**
	 * The column <code>PUBLIC.BOOK_STORE.NAME</code>.
	 */
	public final TableField<Record, String> NAME = createField("NAME",
			org.jooq.impl.SQLDataType.VARCHAR.length(400).nullable(false), this, "");

	/**
	 * Create a <code>PUBLIC.BOOK_STORE</code> table reference
	 */
	public BookStore() {
		this("BOOK_STORE", null);
	}

	/**
	 * Create an aliased <code>PUBLIC.BOOK_STORE</code> table reference
	 */
	public BookStore(String alias) {
		this(alias, BOOK_STORE);
	}

	private BookStore(String alias, Table<Record> aliased) {
		this(alias, aliased, null);
	}

	private BookStore(String alias, Table<Record> aliased, Field<?>[] parameters) {
		super(alias, null, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Schema getSchema() {
		return Public.PUBLIC;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<Record>> getKeys() {
		return Arrays.<UniqueKey<Record>>asList(Keys.CONSTRAINT_F);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BookStore as(String alias) {
		return new BookStore(alias, this);
	}

	/**
	 * Rename this table
	 */
	@Override
	public BookStore rename(String name) {
		return new BookStore(name, null);
	}

}

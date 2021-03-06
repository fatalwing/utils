/**
 * Package that contains abstractions needed to support optional
 * non-blocking decoding (parsing) functionality.
 * Although parsers are constructed normally via
 * {@link com.townmc.utils.jackson.core.JsonFactory}
 * (and are, in fact, sub-types of {@link com.townmc.utils.jackson.core.JsonParser}),
 * the way input is provided differs.
 *
 * @since 2.9
 */

package com.townmc.utils.jackson.core.async;

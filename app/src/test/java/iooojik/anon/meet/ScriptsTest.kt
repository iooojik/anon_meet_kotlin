package iooojik.anon.meet

import org.junit.Test

class ScriptsTest {
    @Test
    fun testEmailCheck() {
        assert(isEmail("qqqq@qq.com"))
        assert(isEmail("1@rr.com"))

        assert(!isEmail("qqqq@.com"))
        assert(!isEmail("qqqq.com"))
        assert(!isEmail("1.com"))
        assert(!isEmail("1q@rr."))
        assert(!isEmail("1q@rr"))
        assert(!isEmail("@rr.com"))
    }

    @Test
    fun testRegex() {
        assert("qqqq" == getLoginFromEmail("qqqq@qq.com"))
    }
}
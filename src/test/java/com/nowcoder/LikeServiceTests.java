package com.nowcoder;

import com.nowcoder.service.LikeService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author JiafengLiu
 * @date 2019/4/7 15:39
 * Email jfliu_2017@stu.xidian.edu.cn
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class LikeServiceTests {
	@Autowired
	LikeService likeService;

	@Before
	public void setUp(){
		System.out.println("setUp");
	}

	@After
	public void tearDown(){
		System.out.println("tearDown");
	}

	@BeforeClass
	public static void beforeClass(){
		System.out.println("beforeClass");
	}

	@AfterClass
	public static void afterClass(){
		System.out.println("afterClass");
	}

	@Test
	public void testLike(){
		System.out.println("testLike");
		likeService.like(123,1,1);
		Assert.assertEquals(1,likeService.getLikeStatus(123,1,1));

		likeService.disLike(123,1,1);
		Assert.assertEquals(-1,likeService.getLikeStatus(123,1,1));

	}

	@Test
	public void testXXX(){
		System.out.println("testXXX");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testException(){
		System.out.println("testException");
		throw new IllegalArgumentException("异常发生了");
	}
}

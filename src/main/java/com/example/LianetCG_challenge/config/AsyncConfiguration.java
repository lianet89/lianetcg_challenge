package com.example.LianetCG_challenge.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync (proxyTargetClass=true)
public class AsyncConfiguration implements AsyncConfigurer{
	public int corePoolSize = 5;
	public int maxPoolSize = 8;
	
	@Bean(name = "threadPoolExecutor")
	  public Executor threadPoolExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(corePoolSize);
	    executor.setMaxPoolSize(maxPoolSize);
	    executor.setThreadNamePrefix("CHALLENGE_");
	    executor.initialize();
	    return executor;
	  }

	  @Override
	  public Executor getAsyncExecutor() {
	    return threadPoolExecutor();
	  }

}

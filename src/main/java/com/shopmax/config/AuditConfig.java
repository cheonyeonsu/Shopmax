package com.shopmax.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration // bean객체를 싱글톤으로 사용하게 만들어주므로 bean이랑 짝꿍처럼 써줌. 
@EnableJpaAuditing // jpa의 auditing 기능 활성화. 
public class AuditConfig {

	@Bean //auditorProvider이름으로 빈객체 등록할게! 
	public AuditorAware<String> auditorProvider(){
		return new AuditorAwareImpl();
	}
}

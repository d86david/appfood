package com.dsys.appfood.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.dsys.appfood.domain.enums.StatusPedido;

import jakarta.persistence.*;

@Entity
@Table(name = "status_pedido_status")
public class StatusPedidoHistorico {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Enumerated(EnumType.STRING)
	private StatusPedido status;
	
	@Column(name = "data_hora")
	private LocalDateTime dataHora;
	
	@ManyToOne
	@JoinColumn(name = "pedido_id")
	private Pedido pedido;
	
	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;
	

	//===============================
	//CONSTRUTORES 
	//===============================
	
	public StatusPedidoHistorico() {
		
	}

	public StatusPedidoHistorico(Integer id, StatusPedido status, LocalDateTime dataHora, Pedido pedido,
			Usuario usuario, String motivo) {
		super();
		this.id = id;
		this.status = status;
		this.dataHora = dataHora;
		this.pedido = pedido;
		this.usuario = usuario;
	}
	
	//===============================
	//GETTERS E SETTERS 
	//===============================

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public StatusPedido getStatus() {
		return status;
	}

	public void setStatus(StatusPedido status) {
		this.status = status;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	//===============================
	//hashcode e equals
	//===============================

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatusPedidoHistorico other = (StatusPedidoHistorico) obj;
		return Objects.equals(id, other.id);
	}
	
	

}

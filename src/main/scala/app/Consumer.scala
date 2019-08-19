package app

trait Consumer[K, V] {

  def subscribe(topic: String): Unit

  def poll(timeout: Long): Iterator[(K, V)]

  def wakeup(): Unit

  def close(): Unit

}
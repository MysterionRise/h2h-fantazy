package org.mystic.cache

import java.util.concurrent.TimeUnit

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.{ClientConfig, ClientNetworkConfig}
import com.hazelcast.core.{Hazelcast, IMap}

import scala.util.Random

object Main {

  def main(args: Array[String]): Unit = {

    val rand = new Random()

    args(0) match {
      case "read" => {
        val networkConfig = new ClientNetworkConfig().addAddress("10.132.0.2", "10.132.0.3", "10.132.0.4", "10.132.0.5")
        val clientConfig = new ClientConfig().setNetworkConfig(networkConfig)
        val hz = HazelcastClient.newHazelcastClient(clientConfig)
        println(hz.getName)
        val skus: IMap[Int, Int] = hz.getMap("skus")

        while (true) {
          val skuToBuy = rand.nextInt(SKU_SIZE)
          var flag = true
          while (flag) {
            if (skus.containsKey(skuToBuy)) {
              val oldValue = skus.get(skuToBuy)
              val ammountToBuy = rand.nextInt(oldValue / 10 + 1)
              val newValue = oldValue - ammountToBuy
              if (newValue >= 0 && skus.replace(skuToBuy, oldValue, newValue)) {
                println(s"buying $skuToBuy $ammountToBuy")
                flag = false
              }
            } else {
              flag = false
              println(s"there is no available sku for key $skuToBuy")
            }
          }
          TimeUnit.MILLISECONDS.sleep(200)
        }
      }
      case "write" => {
        val networkConfig = new ClientNetworkConfig().addAddress("10.132.0.2", "10.132.0.3", "10.132.0.4", "10.132.0.5")
        val clientConfig = new ClientConfig().setNetworkConfig(networkConfig)
        val hz = HazelcastClient.newHazelcastClient(clientConfig)
        println(hz.getName)
        val skus: IMap[Int, Int] = hz.getMap("skus")

        while (true) {
          val skuToBuy = rand.nextInt(SKU_SIZE)
          var flag = true
          while (flag) {
            if (skus.containsKey(skuToBuy)) {
              val oldValue = skus.get(skuToBuy)
              val ammountToAdd = rand.nextInt(oldValue / 10 + 1)
              val newValue = oldValue + ammountToAdd
              if (skus.replace(skuToBuy, oldValue, newValue)) {
                println(s"adding $skuToBuy $ammountToAdd")
                flag = false
              }
            } else {
              val ammountToAdd = rand.nextInt(INIT_SIZE + 1)
              skus.put(skuToBuy, ammountToAdd)
              println(s"adding $skuToBuy $ammountToAdd")
              flag = false
            }
          }
          TimeUnit.MILLISECONDS.sleep(200)
        }
      }
      case _ => {
        val hz = Hazelcast.newHazelcastInstance()
        val skus: IMap[Int, Int] = hz.getMap("skus")
      }
    }

  }
}

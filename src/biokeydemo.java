import zk.jni.JavaToBiokey;
/**
 * ZKFinger Online Sdk Demo
 * 适用于B/S架构
 * @author jkq
 * @description 设备型号：ZKFinger Live20R
 * @createDate 2021.6.18
 */
public class biokeydemo
{
	public static void main(String[] args)
	{
		String reg = "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";// "REGSTR";
		String ver = "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";// "VERSTR";
		String test="fali";
		if (JavaToBiokey.NativeToProcess(reg, ver)){
			test="success";
		}
		System.out.printf("verify 9.0,result="+test+"\n");
		
		reg = "TVFTUjIyAAAEEhAFBQUHCc7QAAAcE2kAAAAAgyEgiBKiAacPlQABAIIdnAHcAH4PagBlEmYN6ACJAA0PLhKrAaoPSADAAZcdEQGWACsONgA8EqwMzQAYAEYPZBJMAIMOhAB3AJodXgGdALIPeAB5EmQPqQBZADAMNxKXAbMPRACKAMIdAABwAM4PYQAbEvkOoQA9Ab0PAhMXAFMOtQCeAfAdlwGIAM0PZABvEucMcgDfAEoPyBJqAUgOngDAAX0dcAEyANkPUAAnE/cOIwBXAH8PAxMHAFMMGQDiAMEdLAmp3xIhWOopGLPgtfpCBkfzxGWAgSogmv2fgkJ1qPtygM/6FgYnAf6ecoBWeeZzdz8K4i9neo/e2VeB9g7jj7PzfPgBCHvu5hdWAxMP/QzLAbaAsJMYEaa8IAtOidYD9w2j/FoCFgk3ia4jhmX6I8cHuYf3hcMQ8AG335Nj830/ERZyASuC6G9qhZJs8OoY8hMfC8filOfG4M5Wk4TO7EYDExab4gbnvZFXfbcHvvW69PsZcAGv37/XMgzfEcLuzLf4/cQJBBIDMVlMFw+e9VYh1l1f/06bOqskIQADRB4qBMUsBEicDQA4AFoBwIJDgiUARABXVXhw7cCVw4DDb0fAf9CGxRQAVACnosXSwkSEwsDCBosDEmABXpNcDsWHBHvCgWd4eAbFngRlx8HAwwUAHwAN7P7ZDAAOAoZ4+9FSbgMAGQWJwwgSYhNTwVyABYgZEswVj6lpwkl6ldSDocPBSQnF1R0dwf/9/f4kwQDJCIGOBAAbLviECxMNL6mZfsUFwcDSwcMRAO83WcPFgJONkMQLAKs4SNLDcMHClhbFp0r7/P3A+fjAm/767PzB/f/9wOMDBNtTgcMJAJmlU6XUyMLDDQELoEkyVMHB+10GAMtrOYHBDQAUazAFc4BuBQG8b3Cn3gDPYEf//vz9/D76+u38//7///w7/vns//v9wP3A3gEPb0j///9HwDhU++3+/ztE/0beAQiBSP/+PlP+Ov/7UsH8///AwDv/xewLARSXKXivhQ4SNp0peIOJzgEPvVIqV8AzDsUMvznDdcFsb8HdARSuXVVTPsDA7v867f9LBwAMyvWZaB4ADsta/0eQ/kkeABDZXP/COcFP0zMHAAveJwbAxNECAZ3fdP3QAQTsZUZM//5bBf/60j/BChAXC9N0emUGEZ8Icf6F/w0DAgtiPkr/yBEGC2ZL/8D+wzr+XRYRWVt9MgrUAiJ1RcH/ZAwQyynz63PD/8PAasIQED0XwMDCagjVDjU7n8GNBRChhXTEKgQR5kNtRcEQ511sRVJCABmGAQYTAccAWl8AxACmEoXUAAAZRZcAQFAAAAAAABbFAAQSAzIAAAAAxQBBUA==";
		ver = "TVFTUjIyAAAEEhAFBQUHCc7QAAAcE2kAAAAAgyEgiBKiAacPlQABAIIdnAHcAH4PagBlEmYN6ACJAA0PLhKrAaoPSADAAZcdEQGWACsONgA8EqwMzQAYAEYPZBJMAIMOhAB3AJodXgGdALIPeAB5EmQPqQBZADAMNxKXAbMPRACKAMIdAABwAM4PYQAbEvkOoQA9Ab0PAhMXAFMOtQCeAfAdlwGIAM0PZABvEucMcgDfAEoPyBJqAUgOngDAAX0dcAEyANkPUAAnE/cOIwBXAH8PAxMHAFMMGQDiAMEdLAmp3xIhWOopGLPgtfpCBkfzxGWAgSogmv2fgkJ1qPtygM/6FgYnAf6ecoBWeeZzdz8K4i9neo/e2VeB9g7jj7PzfPgBCHvu5hdWAxMP/QzLAbaAsJMYEaa8IAtOidYD9w2j/FoCFgk3ia4jhmX6I8cHuYf3hcMQ8AG335Nj830/ERZyASuC6G9qhZJs8OoY8hMfC8filOfG4M5Wk4TO7EYDExab4gbnvZFXfbcHvvW69PsZcAGv37/XMgzfEcLuzLf4/cQJBBIDMVlMFw+e9VYh1l1f/06bOqskIQADRB4qBMUsBEicDQA4AFoBwIJDgiUARABXVXhw7cCVw4DDb0fAf9CGxRQAVACnosXSwkSEwsDCBosDEmABXpNcDsWHBHvCgWd4eAbFngRlx8HAwwUAHwAN7P7ZDAAOAoZ4+9FSbgMAGQWJwwgSYhNTwVyABYgZEswVj6lpwkl6ldSDocPBSQnF1R0dwf/9/f4kwQDJCIGOBAAbLviECxMNL6mZfsUFwcDSwcMRAO83WcPFgJONkMQLAKs4SNLDcMHClhbFp0r7/P3A+fjAm/767PzB/f/9wOMDBNtTgcMJAJmlU6XUyMLDDQELoEkyVMHB+10GAMtrOYHBDQAUazAFc4BuBQG8b3Cn3gDPYEf//vz9/D76+u38//7///w7/vns//v9wP3A3gEPb0j///9HwDhU++3+/ztE/0beAQiBSP/+PlP+Ov/7UsH8///AwDv/xewLARSXKXivhQ4SNp0peIOJzgEPvVIqV8AzDsUMvznDdcFsb8HdARSuXVVTPsDA7v867f9LBwAMyvWZaB4ADsta/0eQ/kkeABDZXP/COcFP0zMHAAveJwbAxNECAZ3fdP3QAQTsZUZM//5bBf/60j/BChAXC9N0emUGEZ8Icf6F/w0DAgtiPkr/yBEGC2ZL/8D+wzr+XRYRWVt9MgrUAiJ1RcH/ZAwQyynz63PD/8PAasIQED0XwMDCagjVDjU7n8GNBRChhXTEKgQR5kNtRcEQ511sRVJCABmGAQYTAccAWl8AxACmEoXUAAAZRZcAQFAAAAAAABbFAAQSAzIAAAAAxQBBUA==";
		test="fali";
		
		if (JavaToBiokey.NativeToProcess(reg, ver)){
			test="success";
		}
		System.out.printf("verify 10.0,result="+test+"\n");
	}
}